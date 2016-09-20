/**
 * Copyright (C) 2016 Dr. David H. Akehurst (http://dr.david.h.akehurst.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.akehurst.example.xtext.vertx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.xtext.web.server.IServiceContext;
import org.eclipse.xtext.web.server.IServiceResult;
import org.eclipse.xtext.web.server.IUnwrappableServiceResult;
import org.eclipse.xtext.web.server.XtextServiceDispatcher;

import com.google.gson.Gson;
import com.google.inject.Injector;
import com.google.inject.Provider;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import net.akehurst.example.xtext.vertx.language.web.LanguageWebSetup;

public class Application implements Runnable {

	Vertx vertx;
	Gson gson = new Gson();

	@Override
	public void run() {
		final XtextServiceDispatcher xtext = this.createXTextDispatcher();
		this.startServer(xtext);
	}

	private XtextServiceDispatcher createXTextDispatcher() {
		final Provider<ExecutorService> executorServiceProvider = () -> Executors.newCachedThreadPool();
		final Injector i = new LanguageWebSetup(executorServiceProvider).createInjectorAndDoEMFRegistration();
		final XtextServiceDispatcher xtext = i.getInstance(XtextServiceDispatcher.class);
		return xtext;
	}

	private void startServer(final XtextServiceDispatcher xtext) {
		this.vertx = Vertx.vertx();

		final Router router = Router.router(this.vertx);

		router.route("/lib/*").handler(StaticHandler.create("META-INF/resources/webjars"));
		router.route("/xtext/*").handler(StaticHandler.create("META-INF/resources/xtext"));
		router.route("/orion/*").handler(StaticHandler.create("orion"));
		router.route("/xtext-service/*").handler(CookieHandler.create());
		router.route("/xtext-service/*").handler(BodyHandler.create());
		router.route("/xtext-service/*").handler(SessionHandler.create(LocalSessionStore.create(this.vertx)));
		router.route("/xtext-service/*").handler((context) -> {
			final IServiceContext xCtx = new VertxXTextServiceContext(context);
			final IServiceResult sr = xtext.getService(xCtx).getService().apply();
			final HttpServerResponse response = context.response();
			response.setStatusCode(200);
			response.putHeader("Cache-Control", "no-cache");
			final String encoding = "UTF-8";
			if (sr instanceof IUnwrappableServiceResult && ((IUnwrappableServiceResult) sr).getContent() != null) {
				final IUnwrappableServiceResult unwrapResult = (IUnwrappableServiceResult) sr;
				String _elvis = null;
				final String _contentType = unwrapResult.getContentType();
				if (_contentType != null) {
					_elvis = _contentType;
				} else {
					_elvis = "text/plain";
				}
				response.putHeader("Content-Type", _elvis);
				final String s = unwrapResult.getContent();
				response.end(s, encoding);
			} else {
				response.putHeader("Content-Type", "text/x-json");
				final String s = this.gson.toJson(sr);
				response.end(s, encoding);
			}
		});

		router.route("/*").handler(StaticHandler.create("unsecure"));

		final HttpServer server = this.vertx.createHttpServer();
		server.requestHandler(router::accept).listen(10101);

	}
}
