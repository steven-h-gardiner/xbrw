# xbrw

Cross-Browser ReWrites

This project deploys a chrome extension that only rewrites webpages by
injecting Javascript, CSS, and HTML into the page, in the following
additional environments:

1. trivially, as an unpacked Chrome extension
1. as a Greasemonkey user script, for Firefox users with Greasemonkey installed
1. as a Tampermonkey script, for Chrome users with Tampermonkey installed
1. as a Firefox extension
1. as a (packed) Chrome extension
1. through a proxy, deploy-able in a Google App Engine application

## Scope

This project does not support all Chrome extensions.  Rather, it
supports the subset which rewrite webpages by injecting content.

Examples of Chrome extension functionality not supported:

1. adding anything to the chrome of Chrome, e.g. buttons or toolbars
1. anything not represented inside the content_scripts object in the
   manifest

## The example application

The example application injects a "Hello World" message over top of
every webpage visited.  It demonstrates, in its banal way, the
following features

1. in addition to standard Chrome extension functionality, HTML may be
   injected into the page inside invisible elements (the content may
   be copied and/or repositioned by script)
1. content can be retrieved from additional websites that use the
   https: protocol

## Creating a new application

1. change manifest.json and add scripts as needed to do what you need
   to do (debug as unpacked Chrome extension)
1. reserve a Google App Engine application with name X
1. put X in war/appengine-web.xml
1. ant deploy

At X.appspot.com/downloads.jsp you should see a list of installation
instructions for the various browsers.  Additionally, a link for using
the proxy, available at the same appspot address.

You can additionally tweak the downloads.jsp as you like.


