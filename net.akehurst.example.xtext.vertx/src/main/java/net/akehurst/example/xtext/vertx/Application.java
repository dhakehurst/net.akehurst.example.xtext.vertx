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

import org.eclipse.xtext.web.server.XtextServiceDispatcher;

import com.google.inject.Injector;
import com.google.inject.Provider;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import net.akehurst.example.xtext.vertx.language.web.LanguageWebSetup;

public class Application implements Runnable {

	public Application(final Integer port, final String htmlDirectory) {
		this.port = null == port ? 8080 : port;
		this.htmlDirectory = null == htmlDirectory ? "withOrion" : htmlDirectory;
	}

	int port;
	String htmlDirectory;

	Vertx vertx;

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
		router.route("/xtext-service/*").handler(VertxXTextHandler.create(xtext));

		router.route("/*").handler(StaticHandler.create(this.htmlDirectory));

		final HttpServer server = this.vertx.createHttpServer();
		server.requestHandler(router::accept).listen(this.port);

	}
}
