var xbrwdl = {};
xbrwdl.mods = {};
xbrwdl.mods.fs = require('fs');
xbrwdl.mods.cp = require('child_process');

xbrwdl.download_sources = function(manfile) {
  var spec = {};
  spec.json = JSON.parse(xbrwdl.mods.fs.readFileSync(manfile, 'utf8'));

  spec.parent = manfile.split(/\//).slice(0,-1).join("/");  
  console.error("PAR: %s", spec.parent);
  
  console.error("JSON: %j", spec.json.sources);

  for (key in spec.json.sources) {
    var curl = xbrwdl.mods.cp.spawn('curl', [
					     '-o', key,
					     '-z', key,
					     spec.json.sources[key]
					     ],
				    {cwd: spec.parent});

    curl.stdout.pipe(process.stdout);
    curl.stderr.pipe(process.stderr);
  }
};

xbrwdl.download_sources(process.argv[2]);
