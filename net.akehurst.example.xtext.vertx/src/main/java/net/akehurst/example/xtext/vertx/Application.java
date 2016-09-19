package net.akehurst.example.xtext.vertx;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.xtext.web.server.IServiceContext;
import org.eclipse.xtext.web.server.IServiceResult;
import org.eclipse.xtext.web.server.ISession;
import org.eclipse.xtext.web.server.IUnwrappableServiceResult;
import org.eclipse.xtext.web.server.XtextServiceDispatcher;
import org.eclipse.xtext.xbase.lib.Functions.Function0;

import com.google.gson.Gson;
import com.google.inject.Injector;
import com.google.inject.Provider;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import net.akehurst.example.xtext.vertx.language.web.MathWebSetup;

public class Application implements Runnable {

	Vertx vertx;
	Gson gson = new Gson();

	@Override
	public void run() {
		final Provider<ExecutorService> executorServiceProvider = () -> Executors.newCachedThreadPool();

		final Injector i = new MathWebSetup(executorServiceProvider).createInjectorAndDoEMFRegistration();

		final XtextServiceDispatcher xtext = i.getInstance(XtextServiceDispatcher.class);

		this.startServer(xtext);
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
			final IServiceResult sr = xtext.getService(this.getServiceContext(context)).getService().apply();
			final HttpServerResponse response = context.response();
			response.setStatusCode(200);
			response.putHeader("Cache-Control", "no-cache");
			// final String _encoding = this.getEncoding(service, result);
			// response.setCharacterEncoding(_encoding);

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
				System.out.println(s);
				response.end(s);
			} else {
				response.putHeader("Content-Type", "text/x-json");
				final String s = this.gson.toJson(sr);
				System.out.println(s);
				response.end(s);
			}
		});

		router.route("/*").handler(StaticHandler.create("unsecure"));

		final HttpServer server = this.vertx.createHttpServer();
		server.requestHandler(router::accept).listen(10101);

	}

	IServiceContext getServiceContext(final RoutingContext vContext) {

		final MultiMap params = vContext.request().params();
		if (!params.contains(IServiceContext.SERVICE_TYPE)) {
			final String serviceType = vContext.request().path().substring("/xtext-service/".length());
			params.add(IServiceContext.SERVICE_TYPE, serviceType);
		}
		final String cT = vContext.request().getHeader("Content-Type");
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

			final String s = vContext.getBodyAsString();
			final String[] encodedParams = s.split("&");
			for (final String param : encodedParams) {
				{
					final int nameEnd = param.indexOf("=");
					if (nameEnd > 0) {
						final String key = param.substring(0, nameEnd);
						final String _substring = param.substring(nameEnd + 1);
						try {
							final String value = URLDecoder.decode(_substring, charset);
							params.add(key, value);
						} catch (final UnsupportedEncodingException e) {
							e.printStackTrace();
						}

					}
				}
			}

		}
		final IServiceContext context = new IServiceContext() {

			@Override
			public ISession getSession() {
				return Application.this.getSession1(vContext.session());
			}

			@Override
			public Set<String> getParameterKeys() {
				return params.names();
			}

			@Override
			public String getParameter(final String key) {
				return params.get(key);
			}
		};

		return context;
	}

	ISession getSession1(final Session session) {
		return new ISession() {

			@Override
			public <T> T get(final Object key) {
				return (T) session.get(key.toString());
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
				session.put(key.toString(), value);
			}

			@Override
			public void remove(final Object key) {
				session.remove(key.toString());
			}

		};
	}
}
