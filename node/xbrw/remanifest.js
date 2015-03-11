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

for (filename in remanifest.manifest.sources) {
  var inurl = remanifest.manifest.sources[filename];

  var outfile = remanifest.mods.path.resolve(remanifest.args.outdir, filename);
  
  var purl = remanifest.mods.url.parse(inurl);  
  var outurl = function(urlobj, url) {
    console.error("PROTOCOL: " + urlobj.protocol);
    if (! urlobj.protocol) {
      var filepath = url;
      var absolute = remanifest.mods.path.resolve(filepath);      
      var newrel = remanifest.mods.path.relative(remanifest.args.outdir, absolute);
      var local = remanifest.mods.path.basename(filepath);

      remanifest.mods.fs.createReadStream(absolute).pipe(remanifest.mods.fs.createWriteStream(outfile));

      return local;
    }

    var wget = remanifest.mods.cp.spawn('wget', ['-N',
						 inurl
						 ],
    {cwd: remanifest.args.outdir});

    wget.stdout.pipe(process.stdout);
    wget.stderr.pipe(process.stderr);
    
    
    return remanifest.mods.url.format(urlobj);
  }(purl, inurl);
  remanifest.manifest.sources[filename] = outurl;  
}

remanifest.mods.fs.writeFileSync(remanifest.args.outfile, JSON.stringify(remanifest.manifest, null, 2));
