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

import org.eclipse.xtext.web.server.ISession;
import org.eclipse.xtext.xbase.lib.Functions.Function0;

import io.vertx.ext.web.Session;

public class VertxXTextSession implements ISession {
	VertxXTextSession(final Session session) {
		this.session = session;
	}

	Session session;

	// --- ISession ---
	@Override
	public <T> T get(final Object key) {
		return (T) this.session.get(key.toString());
	}

	@Override
	public <T> T get(final Object key, final Function0<? extends T> factory) {
		T t = this.get(key);
		if (null == t) {
			t = factory.apply();
			this.put(key, t);
			return t;
		} else {
			return t;
		}
	}

	@Override
	public void put(final Object key, final Object value) {
		this.session.put(key.toString(), value);
	}

	@Override
	public void remove(final Object key) {
		this.session.remove(key.toString());
	}
}
