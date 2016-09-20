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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Set;

import org.eclipse.xtext.web.server.IServiceContext;
import org.eclipse.xtext.web.server.ISession;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

public class VertxXTextServiceContext implements IServiceContext {

	VertxXTextServiceContext(final RoutingContext context) {
		this.context = context;
		this.initParams();
	}

	RoutingContext context;
	MultiMap params;

	private void initParams() {
		this.params = this.context.request().params();
		if (!this.params.contains(IServiceContext.SERVICE_TYPE)) {
			final String serviceType = this.context.request().path().substring("/xtext-service/".length());
			this.params.add(IServiceContext.SERVICE_TYPE, serviceType);
		}
		final String cT = this.context.request().getHeader("Content-Type");
		if (null != cT) {
			final String contentType[] = cT.split(";(\\s*)");
			String _charset = null;
			if (contentType != null && contentType.length >= 2 && contentType[1].startsWith("charset=")) {
				final String _get = contentType[1];
				final int _length = "charset=".length();
				_charset = _get.substring(_length);
			} else {
				final Charset _defaultCharset = Charset.defaultCharset();
				_charset = _defaultCharset.toString();
			}
			final String charset = _charset;

			final String s = this.context.getBodyAsString();
			final String[] encodedParams = s.split("&");
			for (final String param : encodedParams) {
				{
					final int nameEnd = param.indexOf("=");
					if (nameEnd > 0) {
						final String key = param.substring(0, nameEnd);
						final String _substring = param.substring(nameEnd + 1);
						try {
							final String value = URLDecoder.decode(_substring, charset);
							this.params.add(key, value);
						} catch (final UnsupportedEncodingException e) {
							e.printStackTrace();
						}

					}
				}
			}

		}
	}

	// --- IServiceContext ---
	@Override
	public ISession getSession() {
		return new VertxXTextSession(this.context.session());
	}

	@Override
	public Set<String> getParameterKeys() {
		return this.params.names();
	}

	@Override
	public String getParameter(final String key) {
		return this.params.get(key);
	}
}
