package net.akehurst.example.xtext.vertx.language.web

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provider
import com.google.inject.util.Modules
import java.util.concurrent.ExecutorService
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import net.akehurst.example.xtext.vertx.language.web.MathWebModule
import net.akehurst.example.xtext.vertx.language.MathStandaloneSetup
import net.akehurst.example.xtext.vertx.language.MathRuntimeModule

@FinalFieldsConstructor
class MathWebSetup extends MathStandaloneSetup {
	
	val Provider<ExecutorService> executorServiceProvider;
	
	override Injector createInjector() {
		val runtimeModule = new MathRuntimeModule()
		val webModule = new MathWebModule(executorServiceProvider)
		return Guice.createInjector(Modules.override(runtimeModule).with(webModule))
	}
	
}