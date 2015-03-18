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

## Well that sounds pretty restricted

We just didn't want to get anyone's hopes up.

That said, you can do lots of useful things by rewriting webpages.
For example, the two most popular add-ons for Firefox are page
rewriters (though they're probably implemented differently) and number
8 is Greasemonkey, a tool for installing rewrites in your browser.

## Getting started

To setup a project directory, you probably want

1. some kind of source control
1. a subdirectory called 'lib'
1. download 'xbrw.jar' into 'lib'
1. extract a stub build file for [Apache Ant](http://ant.apache.org) by
   running `java -jar lib/xbrw.jar`

You can add any new targets to the build.xml if needed.

Lastly, you will need a `manifest.json` file.  The format of this is
mostly the same as the format for specifying an unpacked Chrome
extension.  You can either start from scratch, or begin from the
example application, included inside the jarfile.

In addition to the standard Chrome extension functionality provided by
the `content_scripts` list, the following features are supported:

1. Just as chrome extensions list javascript files and css stylesheets
   which will be injected in affected pages, you can list HTML files
   as well.  Their contents will be injected inside hidden elements.
1. all resources may be specified by remove URL by using an additional
   `sources` object in `manifest.json`.  See the example application
   for an example.

## Dependencies

1. [Apache Ant](http://ant.apache.org)
1. [nodejs](http://nodejs.org)
1. [Chromium](http://chromium.org) and/or Google Chrome, for packaging
   Chrome extensions

## The example application

The example application injects a "Hello World" message over top of
every webpage visited.  It demonstrates, in its banal way, the
following features

1. in addition to standard Chrome extension functionality, specified
   HTML will be injected into the page inside invisible elements (the
   content may be copied and/or repositioned by script)   
1. content can be retrieved from additional websites that use the
   https: protocol

To extract the example application, you can use the command `ant
helloworld`.  This command will extract the following pieces:

1. a `manifest.json` file
1. a `helloworld.js` file 
1. a `helloworld.html` file 
1. a `helloworld.css` file

All of these will be injected into rewritten pages.  In addition to
this content, the `manifest.json` file contains references to further
required information, e.g. [the jquery library](http://jquery.com),
from the web.

## Creating a [Greasemonkey](http://greasespot.net) user script for Firefox

To create a Greasemonkey user script, use the `ant greasemonkey`
command.  Based on the contents of your `manifest.json` file, it will
create a `X.user.js` in the `war/` subdirectory (where X is the name
specified in your `manifest.json` file)

To install this user script, simply drag it onto a Firefox browser
with Greasemonkey installed.  Greasemonkey users can install it from a
website by clicking on a link to it.


## Creating a [Tampermonkey](http://tampermonkey.net) user script for Chrome

To create a Tampermonkey user script, use the `ant tampermonkey`
command.  This will create `war/X.tamper.js` (where X is the name
specified in your `manifest.json` file)

To install this user script, simply drag it onto a Chrome browser with
Tampermonkey installed.  Tampermonkey users can install it from a
website by clicking a link to it.

Note: the `X.tamper.js` file is a literal copy of the `X.user.js` file;
Tampermonkey requires the `.tamper.js` suffix for installation.

## Creating a standalone Firefox extension

To create a Firefox extension, use the `ant xpi` command.  This will
create the file `war/X.xpi`.

To install this extension, simply drag it onto Firefox.  Firefox users
can install the extension by clicking a link to it.

Note: The standalone Firefox extension is implemented by packaging the
`X.user.js` greasemonkey script described above alongside the
Greasemonkey extension itself, then hiding the Greasemonkey button.

## Creating a packaged Chrome extension

To create a packaged Chrome extension, use the `ant crx` command.
This will create the file `war/X.crx.`

To install this extension, simply drag it onto the Extensions pane of
Chrome.  Chrome users can install the extension by downloading it from
a link, then dragging it onto their Extensions pane.  Chrome security
disables extensions from third-party websites, necessitating this
workaround.

## Running a proxy server on your local machine

To run a proxy server that injects your code into every page, you'll
need to download the Google App engine SDK by using the `ant sdk`
command.

Then execute the `ant runserver` command.  The proxy server will run
as long as this task continues (to stop the proxy server, just
terminate this command)

To test that the proxy server is running, point your browser (any
browser!) at [http://localhost:9090/](http://localhost:9090).  This
will display a basic search form, and you can type the URL of the page
you want to visit.

You can skip the search page by simply suffixing the desired URL onto
the end of `http://localhost:9090/mirror/`

## Deploying a proxy server on Google App Engine

To deploy to Google App Engine, you'll need the following

1. download the SDK, as above: `ant sdk`
1. reserve a Google App Engine application with name Y
1. 

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


