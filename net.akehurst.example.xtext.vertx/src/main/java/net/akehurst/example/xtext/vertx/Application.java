package net.akehurst.example.xtext.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class Application implements Runnable {

	Vertx vertx;

	@Override
	public void run() {
		this.vertx = Vertx.vertx();

		final Router router = Router.router(this.vertx);

		router.route("/lib/*").handler(StaticHandler.create("META-INF/resources/webjars"));
		router.route("/orion/*").handler(StaticHandler.create("orion"));

		router.route("/*").handler(StaticHandler.create("unsecure"));

		final HttpServer server = this.vertx.createHttpServer();
		server.requestHandler(router::accept).listen(10101);

	}

}
