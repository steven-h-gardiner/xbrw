(function(jQuery) {
  console.error("POLY");
  if (!window.chrome) { console.error("POLY NOCHROME"); return; }
  if (!chrome.runtime) { console.error("POLY NORUN"); return; }
  if (!chrome.extension) { console.error("POLY NOEXT"); return; }
  if (!chrome.extension.getURL) { console.error("POLY NOGET"); return; }

  // if above statements did not return, then we are running inside a chrome extension

  window.xbrw = {};
  window.xbrw.manifest = chrome.runtime.getManifest();
  
  chrome.pjQuery = jQuery;

  var syncFetch = function(url, cType) {
    var output = false;
    var xhr = new XMLHttpRequest();
    cType = cType || "json";
    
    xhr.onreadystatechange = function() {
      if (xhr.readyState == 4) {
	switch (cType) {
	  case "text":
	    output = xhr.responseText;
	    break;
	  default:
	    output = JSON.parse(xhr.responseText);
	}
      }
    };
    xhr.open("GET", chrome.extension.getURL(url), false);

    try {
      xhr.send();
    } catch(e) {
      console.log("Couldn't load '%s'", url);
    }

    return output;
  };
  
  //chrome.manifestjson = syncFetch("/manifest.json");
  chrome.manifestjson = chrome.runtime.getManifest();
    
  chrome.manifestjson.content_scripts.forEach(function(script) {
    script.html.forEach(function(html) {
      var spec = {};
      spec.url = ['',html].join('/');
      console.error("CHROME FETCH: " + spec.url);
      spec.strContents = syncFetch(spec.url, "text");
      console.error("GOT: " + spec.strContents);

      spec.inject = function() {
	  
	var div = document.createElement("div");
	div.setAttribute("style", ["display", "none"].join(":"));
	div.setAttribute('data-src', spec.url);
	div.innerHTML = spec.strContents;
	document.body.appendChild(div);
      };

      if (chrome.pjQuery) {
	chrome.pjQuery(document).on('ready', spec.inject);
      } else {
	document.addEventListener('DOMContentLoaded', spec.inject);	
      }
    });
  });
}(window.jQuery));
