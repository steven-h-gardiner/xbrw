var xbrwgm = {};
xbrwgm.mods = {};
xbrwgm.mods.fs = require('fs');
xbrwgm.mods.cp = require('child_process');

xbrwgm.convert = function(manfile, gmfile) {
  var spec = {};
  spec.json = JSON.parse(xbrwgm.mods.fs.readFileSync(manfile, 'utf8'));

  spec.out = xbrwgm.mods.cp.spawn('tee', [gmfile]);

  spec.out.stdout.pipe(process.stdout);
  spec.out.stderr.pipe(process.stderr);

  spec.header = [];
  spec.body = [];
  spec.fdefs = {};
  
  spec.header.push("// @name " + spec.json.name);
  if (spec.json.description) {
    spec.header.push("// @description " + spec.json.description);
  }
  spec.json.content_scripts.forEach(function(script) {
    script.js.forEach(function(js) {
      var src = spec.json.sources[js] || js;
      spec.header.push("// @require " + src);  
    });
    var reznum = 0;
    script.css.forEach(function(css) {
      reznum++;
      var cssid = ["css",reznum].join("");
      var src = spec.json.sources[css] || css;
      spec.header.push("// @resource " + cssid + " "  + src);
      spec.body.push('GM_addStyle(GM_getResourceText("' + cssid + '"));');
    });
    script.html.forEach(function(html) {
      reznum++;
      var htmlid = ["html",reznum].join("");
      var src = spec.json.sources[html] || html;
      spec.header.push("// @resource " + htmlid + " "  + src);
      spec.fdefs.injectHTML = [
			       "function(txt) {",
			       "  var div = document.createElement('div');",
			       "  div.setAttribute('style', 'display: none');",
			       "  div.innerHTML = txt;",
			       "  document.body.appendChild(div);",
			       "}"
			       ].join("\n");
      spec.body.push('injectHTML(GM_getResourceText("' + htmlid + '"));');
    });
  });

  spec.header.push("// @resource manifest manifest.json");
  spec.body.push("\ntry {");
  spec.body.push("console.error('CHROMEDEF');");
  spec.body.push("\nwindow.xbrw = {};");
  spec.body.push("window.xbrw.manifest = JSON.parse(GM_getResourceText('manifest'));");
  spec.body.push("console.error('/CHROMEDEF');");
  spec.body.push("} catch (cee) { console.error('CHROMEDEF ERR: ' + cee); }");
  
  spec.header.push("// @grant GM_getResourceText");
  spec.header.push("// @grant GM_addStyle");
  spec.header.push("// @grant GM_xmlhttpRequest");
  spec.header.push("// @include *");
  
  spec.out.stdin.write("// ==UserScript==\n");
  spec.out.stdin.write(spec.header.join("\n"));
  spec.out.stdin.write("\n");
  spec.out.stdin.write("// ==/UserScript==\n");
  spec.out.stdin.write("\n\n");
  for (fname in spec.fdefs) {
    spec.out.stdin.write(["var",fname,"=",spec.fdefs[fname],";"].join(" "));
  }
  spec.out.stdin.write("\n\n");
  spec.out.stdin.write(spec.body.join("\n"));
  spec.out.stdin.write("\n\n");
  spec.out.stdin.end();  
};

xbrwgm.convert(process.argv[2], process.argv[3]);
