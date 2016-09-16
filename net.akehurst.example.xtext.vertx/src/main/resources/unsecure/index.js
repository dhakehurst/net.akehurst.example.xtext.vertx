require.config({
    bundles: {
        "orion/code_edit/built-codeEdit-amd": ["orion/codeEdit", "orion/Deferred"]
    }	    
});
require(["orion/codeEdit", "orion/Deferred"], function(mCodeEdit, Deferred) {
	var codeEdit = new mCodeEdit();

	codeEdit.create({
		parent: "editor"
	}).then(function(editorViewer) {
		editorViewer.setContents("Hello", "text/plain")
	});
});


