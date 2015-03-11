var xbrwdl = {};
xbrwdl.mods = {};
xbrwdl.mods.fs = require('fs');
xbrwdl.mods.cp = require('child_process');

xbrwdl.download_sources = function(manfile, dest) {
  var spec = {};
  spec.json = JSON.parse(xbrwdl.mods.fs.readFileSync(manfile, 'utf8'));

  spec.parent = manfile.split(/\//).slice(0,-1).join("/");
  spec.dest = dest || spec.parent;  
  console.error("PAR: %s", spec.parent);
  
  console.error("JSON: %j", spec.json.sources);

  for (key in spec.json.sources) {
    var curl = remanifest.mods.cp.spawn('curl', [
					     '-o', key,
					     '-z', key,
					     spec.json.sources[key]
					     ],
				    {cwd: spec.dest});

    curl.stdout.pipe(process.stdout);
    curl.stderr.pipe(process.stderr);
  }
};

xbrwdl.download_sources(process.argv[2], process.argv[3]);
