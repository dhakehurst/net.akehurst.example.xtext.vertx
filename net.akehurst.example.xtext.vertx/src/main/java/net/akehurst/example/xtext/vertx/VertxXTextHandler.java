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

import org.eclipse.xtext.web.server.IServiceContext;
import org.eclipse.xtext.web.server.IServiceResult;
import org.eclipse.xtext.web.server.IUnwrappableServiceResult;
import org.eclipse.xtext.web.server.XtextServiceDispatcher;

import com.google.gson.Gson;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class VertxXTextHandler implements Handler<RoutingContext> {

	static public VertxXTextHandler create(final XtextServiceDispatcher xtext) {
		return new VertxXTextHandler(xtext);
	}

	public VertxXTextHandler(final XtextServiceDispatcher xtext) {
		this.xtext = xtext;
		this.gson = new Gson();
	}

	XtextServiceDispatcher xtext;
	Gson gson;

	@Override
	public void handle(final RoutingContext context) {
		final IServiceContext xCtx = new VertxXTextServiceContext(context);
		final IServiceResult sr = this.xtext.getService(xCtx).getService().apply();
		final HttpServerResponse response = context.response();
		response.setStatusCode(200);
		response.putHeader("Cache-Control", "no-cache");
		final String encoding = "UTF-8";
		if (sr instanceof IUnwrappableServiceResult && ((IUnwrappableServiceResult) sr).getContent() != null) {
			final IUnwrappableServiceResult unwrapResult = (IUnwrappableServiceResult) sr;
			String contentType = unwrapResult.getContentType();
			if (contentType == null) {
				contentType = "text/plain";
			}
			response.putHeader("Content-Type", contentType);
			final String s = unwrapResult.getContent();
			response.end(s, encoding);
		} else {
			response.putHeader("Content-Type", "text/x-json");
			final String s = this.gson.toJson(sr);
			response.end(s, encoding);
		}
	}

}
