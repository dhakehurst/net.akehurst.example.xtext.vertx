/*
 * generated by Xtext unknown
 */
package net.akehurst.example.xtext.vertx.language.ide

import com.google.inject.Guice
import net.akehurst.example.xtext.vertx.language.MathRuntimeModule
import net.akehurst.example.xtext.vertx.language.MathStandaloneSetup

/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class MathIdeSetup extends MathStandaloneSetup {

	override createInjector() {
		Guice.createInjector(new MathRuntimeModule, new MathIdeModule)
	}
}
