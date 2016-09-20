define([], function() {
	var keywords = "package|enum|primitive|entity";
	return {
		id: "xtext.language",
		contentTypes: ["xtext/language"],
		patterns: [
			{include: "orion.c-like#comment_singleLine"},
			{include: "orion.c-like#comment_block"},
			{include: "orion.lib#string_doubleQuote"},
			{include: "orion.lib#string_singleQuote"},
			{include: "orion.lib#number_decimal"},
			{name: "keyword.query", match: "\\b(?:" + keywords + ")\\b"}
		]
	};
});