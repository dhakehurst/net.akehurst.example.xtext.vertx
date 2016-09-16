package net.akehurst.example.xtext.vertx;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.xtext.web.server.IServiceContext;
import org.eclipse.xtext.web.server.ISession;
import org.eclipse.xtext.web.server.XtextServiceDispatcher;

import com.google.inject.Injector;
import com.google.inject.Provider;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import net.akehurst.example.xtext.vertx.language.web.MathWebSetup;

public class Application implements Runnable {

	Vertx vertx;

	@Override
	public void run() {
		final Provider<ExecutorService> executorServiceProvider = () -> Executors.newCachedThreadPool();

		final Injector i = new MathWebSetup(executorServiceProvider).createInjectorAndDoEMFRegistration();

		final XtextServiceDispatcher xtext = i.getInstance(XtextServiceDispatcher.class);

		final Map<String, String> params = new HashMap<>();
		params.put(IServiceContext.SERVICE_TYPE, "highlight");
		params.put("fullText", "true");
		final IServiceContext context = new IServiceContext() {

			@Override
			public ISession getSession() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<String> getParameterKeys() {
				return params.keySet();
			}

			@Override
			public String getParameter(final String key) {
				return params.get(key);
			}
		};
		xtext.getService(context);

		this.startServer();
	}

	private void startServer() {
		this.vertx = Vertx.vertx();

		final Router router = Router.router(this.vertx);

		router.route("/lib/*").handler(StaticHandler.create("META-INF/resources/webjars"));
		router.route("/xtext/*").handler(StaticHandler.create("META-INF/resources/xtext"));
		router.route("/orion/*").handler(StaticHandler.create("orion"));

		router.route("/*").handler(StaticHandler.create("unsecure"));

		final HttpServer server = this.vertx.createHttpServer();
		server.requestHandler(router::accept).listen(10101);

	}

}
