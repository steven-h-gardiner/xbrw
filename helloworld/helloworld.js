(function(jQuery) {
  var hello = {};
  hello.jQuery = jQuery;

  hello.jQuery(document).on('ready', function() {
    hello.jQuery('#helloworld').appendTo(hello.jQuery("body"));

    hello.jQuery.get("https://www.telize.com/geoip",
		     {},
		     function(data,status) {
		       setTimeout(function() {
			 hello.jQuery("#greetee").text(data.city);
		       }, 1000);		       
		     },
		     "json");
    
  });
 
}(window.jQuery));
