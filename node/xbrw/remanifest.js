var remanifest = {};

remanifest.mods = {};
remanifest.mods.fs = require('fs');
remanifest.mods.path = require('path');
remanifest.mods.url = require('url');
remanifest.mods.cp = require('child_process');

remanifest.args = {};
remanifest.args.infile = process.argv[2];
remanifest.args.outfile = process.argv[3];

remanifest.args.outdir = remanifest.mods.path.resolve(remanifest.mods.path.dirname(remanifest.args.outfile));
console.error("ARGS: %j", remanifest.args);

remanifest.manifest = JSON.parse(remanifest.mods.fs.readFileSync(remanifest.args.infile));

remanifest.rewrite = function(inurl) {
  switch (inurl) {
    case "inject-html.js":
    case "jquery-noconflict.js":
      return inurl;
  }

    
  var outfile = remanifest.mods.path.resolve(remanifest.args.outdir, filename);
  
  var purl = remanifest.mods.url.parse(inurl);  
  var outurl = function(urlobj, url) {
    console.error("PROTOCOL: " + urlobj.protocol);
    if (! urlobj.protocol) {
      var info = {};
      info.filepath = url;
      info.absolute = remanifest.mods.path.resolve(info.filepath);      
      info.newrel = remanifest.mods.path.relative(remanifest.args.outdir, info.absolute);
      info.local = remanifest.mods.path.basename(info.filepath);
      info.outfile = remanifest.mods.path.resolve(remanifest.args.outdir, info.local);

      setTimeout(function() {
	console.error("FILEINFO %j", info);
      
	remanifest.mods.fs.createReadStream(info.absolute).pipe(remanifest.mods.fs.createWriteStream(info.outfile));
      }, 0);

      return info.local;
    }

    var wget = remanifest.mods.cp.spawn('wget', ['-N',
						 inurl
						 ],
    {cwd: remanifest.args.outdir});

    wget.stdout.pipe(process.stdout);
    wget.stderr.pipe(process.stderr);
    
    
    return remanifest.mods.url.format(urlobj);
  }(purl, inurl);
  return outurl;
};

remanifest.maybeRewrite = function(url) {
  if (remanifest.manifest.sources[url]) { return url; }
  return remanifest.rewrite(url);
};

console.error("SOURCES");
for (filename in remanifest.manifest.sources) {
  remanifest.manifest.sources[filename] = remanifest.rewrite(remanifest.manifest.sources[filename]);
}

remanifest.manifest.content_scripts.forEach(function(script) {
    console.error("JS");
  script.js = script.js.map(remanifest.maybeRewrite);
  console.error("CSS");
  script.css = script.css.map(remanifest.maybeRewrite);
  console.error("HTML");
  script.html = script.html.map(remanifest.maybeRewrite);
});

remanifest.mods.fs.writeFileSync(remanifest.args.outfile, JSON.stringify(remanifest.manifest, null, 2));
