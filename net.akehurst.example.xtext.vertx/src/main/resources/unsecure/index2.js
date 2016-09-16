

require.config({
    paths: {
        "text": "lib/requirejs-text/3.2.1/text",
        "jquery": "lib/jquery/3.1.0/jquery.min",
        "xtext/xtext-orion": "xtext/2.11.0-SNAPSHOT/xtext-orion"
    }
});

require(["orion/code_edit/built-codeEdit-amd"], function() {
    require(["xtext/xtext-orion"], function(xtext) {
        xtext.createEditor({
        	parent:'editor',
        	xtextLang: 'language'
        });
    });
});