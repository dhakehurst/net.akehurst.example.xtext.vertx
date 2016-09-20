var baseUrl = window.location.pathname;
var fileIndex = baseUrl.indexOf("index.html");
if (fileIndex > 0)
	baseUrl = baseUrl.slice(0, fileIndex);
require.config({
	baseUrl: baseUrl,
	paths: {
		"jquery": "lib/jquery/3.1.0/jquery.min",
		"ace/ext/language_tools": "lib/ace/1.2.2/src/ext-language_tools",
		"xtext/xtext-ace": "xtext/2.11.0-SNAPSHOT/xtext-ace"
	}
});
require(["lib/ace/1.2.2/src/ace"], function() {
	require(["xtext/xtext-ace"], function(xtext) {
		xtext.createEditor({
			baseUrl: baseUrl,
			syntaxDefinition: "xtext-resources/generated/mode-mydsl"
		});
	});
});