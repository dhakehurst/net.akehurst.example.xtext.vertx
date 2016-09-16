require.config({
    paths: {
        "text": "webjars/requirejs-text/<version>/text",
        "jquery": "webjars/jquery/<version>/jquery.min",
        "xtext/xtext-orion": "xtext/<version>/xtext-orion"
    }
});
require(["orion/code_edit/built-codeEdit-amd"], function() {
    require(["xtext/2.11.0-SNAPSHOT/xtext-orion"], function(xtext) {
        xtext.createEditor();
    });
});