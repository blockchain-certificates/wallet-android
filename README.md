# learning-machine-android

[![Build Status](https://travis-ci.org/blockchain-certificates/wallet-android.svg?branch=master)](https://travis-ci.org/blockchain-certificates/wallet-android)

[Blockcerts](https://www.blockcerts.org) Android application by Learning Machine

## Build variants

Gradle allows us to define different product variants. Each has a separate app id and can be installed simultaneously. Currently they all use the Bitcoin main net and do not differ all that much. Production has an empty logging tree.

* `dev`
  * App id: `com.learningmachine.android.app.dev`
  * Dimension: `env`
* `staging`
  * App id: `com.learningmachine.android.app.staging`
  * Dimension: `env`
* `production`
  * App id: `com.learningmachine.android.app`
  * Dimension: `env`

## Running Tests

`sh scripts/run_tests.sh`

## Building APKs

`sh scripts/GenerateReleaseBuild.command`
Answer the prompts as they come.
Blockcerts team members: password is stored in regular password manager.

## Glossary

## Conventions

The following are guidelines for this Android project.

### Classes

* Naming
  * Suffix with the type of class: `Activity`, `Fragment`, `Listener`, `ListItemView`, `Manager`
  * Prefix member variables with `m`.
  * If a variable is a view, consider suffixing with the type: `mPasswordEditText`, `mLoginButton`
  * Boolean member variables should reflect state (`mLocked`) and have getters using `isState()` (ie: `isLocked`)
* Extract common elements to a superclass to avoid duplication.
* Prefer `abstract` methods in super over implicit setup in subclass constructors.
* The suggested template below should guide naming:

```java
public class TemplateFragment {

    private static final String ARG_CONSTANT = "TemplateFragment.Constant";

    private List<Object> mObjects;

    /* static methods */

    public static TemplateFragment newInstance() {
        return new TemplateFragment();
    }

    /* lifecycle overrides, onSavedInstanceState, options menu, onActivityResult */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObjects = new ArrayList<Object>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // databinding & init
        initView();
    }

    /* private methods */

    private void initView() {}

    private void setupData() {}

    /* getters, setters, and other public methods */

    public List<Object> getObjects() {
        return mObjects;
    }

    public void setObjects(List<Objects> objects) {
        mObjects = objects;
    }

    /* enums */

    protected enum ObjectItemType {
        ITEM_TYPE_A,
        ITEM_TYPE_B,
    }

    /* inner classes */

    private class TemplateListAdapter extends ArrayAdapter<ObjectItemType> {

        public EnergyListAdapter(List<ObjectItemType> items) {
            super(getActivity(), 0, items);
        }
    }

    /* base class overrides */

    @Override
    protected boolean usesInjection() {
        return true;
    }
}
```

We suggest the following method orgainization:

1. newInstance and other static methods

* lifecycle, onSavedInstanceState, options menu, onActivityResult
* private methods
* all other protected methods (including immediate parent overrides)
* all other public methods(including immediate parent overrides)
* enums
* defined interfaces
* inner classes
* base overrides

### Layout files

* Naming
  * Reverse DNS style.
  * Prefix with the type of layout: `activity_login`, `fragment_login`, `list_item_login`
  * Describe widgets and suffix with the type: `login_username_text_view`
* Use auto format for consistency.
* Prefer `/>` over open and close tags that do not contain children.
* Extract strings and dimensions to their respective xml resources. `0dp` is an exception and should remain in the layout file.
* Use styles when possible.
* id's should come first for easy element identification, and be on a new line. Enable `Preferences > Code Style > XML > Android > Insert Line break before first attribute` to help format your layout files properly.

### `strings.xml`

* Naming
  * Describe where it is used: `fragment_login_username_hint`
  * For long strings, append `_message`: "We would like to welcome you to this app because it is amazing" becomes `fragment_login_welcome_message`
  * If error, append `_error_message`: "Failed to login" would be `login_error_message`
* Formatted strings
  * Use the [recommended pattern](http://developer.android.com/guide/topics/resources/string-resource.html#FormattingAndStyling): `%1$s`
  * Use the correct type for the data it will be formatting: `s`, `d`, `f`, etc.

### Managers

* Responsibilities
  * Loading and providing data
  * Creating success and failure callbacks
  * Fetching / pushing data from / to the `WebService`

* Prefix data operations with `load`, this signifies an asynchronous call (i.e. webservice or database)
* Prefix direct data accessors with `get`. These methods will synchronously return data directly to the caller.

### Stores

* Controllers (i.e. Activities and Fragments) should never access Stores directly. All requests to load or get data should be relayed through a Manager.

### Interfaces / Callbacks

* We like and make extensive use of these.
* They are a great way to enforce implementation of something a calling class may need.

## Blockcerts Libraries

### Cert-verifier-js

* Javascript library for verifying Blockcerts Certificates

#### Updating cert-verifier-js to a new version

Pull down the cvjs repository:

```
https://github.com/blockchain-certificates/cert-verifier-js.git && cd cert-verifier-js
```

CVJS requires an npm token, so create one on [npm](https://docs.npmjs.com/creating-and-viewing-authentication-tokens)

```
export NPM_TOKEN={insert token here}
```

Install

```
npm install
```

Generate build

```
npm run-script build
```

Copy content of `/dist/verifier-iife.js`

Paste in this android project at this location: `wallet-android/LearningMachine/app/src/main/assets/www/verifier.js`

## External Libraries

### [Autolinker.js](https://github.com/gregjacobs/Autolinker.js)

* Utility to convert urls found in HTML into links
* Minified javascript included as a static asset in the `www` directory of the app
* MIT

### [BitcoinJ](https://bitcoinj.github.io/)

* Java implementation of the Bitcoin protocol
* [Forked](https://github.com/uniquid/bitcoinj/tree/master-uniquid) by uniquid to allow creation of wallets with arbitrary path (e.g m/44'/0'/0/0)
* Apache License

### [SLF4J](https://www.slf4j.org/)

* Simple Logging Facade for Java
* Required by BitcoinJ
* MIT

### [Spongy Castle](https://rtyley.github.io/spongycastle/)

* Repackage of Bouncy Castle for Android
* Required by BitcoinJ
* MIT X11

### [Protobuf](https://developers.google.com/protocol-buffers/)

* Google's language-neutral, platform-neutral, extensible mechanism for serializing structured data
* Required by BitcoinJ
* [License](https://github.com/google/protobuf/blob/master/LICENSE)

### [Dagger 2](https://google.github.io/dagger/)

* Dependency injection framework
* Apache 2.0

### [Timber](https://github.com/JakeWharton/timber)

* A logger with a small, extensible API which provides utility on top of Android's normal Log class
* Apache 2.0

### [JodaTime Android](https://github.com/dlew/joda-time-android)

* A version of [Joda-Time](http://www.joda.org/joda-time/) built with Android in mind
* Apache 2.0

### [Retrofit](http://square.github.io/retrofit/)

* A type-safe REST client for Android and Java
* Apache 2.0

### [Okhttp](http://square.github.io/okhttp/)

* An HTTP+HTTP/2 client for Android and Java applications
* Apache 2.0

### [Gson](https://code.google.com/p/google-gson/)

* JSON object converter and parser
* Apache 2.0

### [RxJava](https://github.com/ReactiveX/RxAndroid)

* [RxJava](https://github.com/ReactiveX/RxJava) bindings for Android
* Apache 2.0

### [RxLifecycle](https://github.com/trello/RxLifecycle)

* Automatically complete sequences based on a second lifecycle stream
* Apache 2.0

### [RxLint](https://github.com/littlerobots/rxlint)

* A set of lint checks that check your RxJava code
* Apache 2.0

### [Picasso](http://square.github.io/picasso/)

* Image loading and caching
* Apache 2.0

### [Guava](https://github.com/google/guava)

* Google Core Libraries for Java
* Apache 2.0

### [JUnit](http://junit.org/)

* A simple framework to write repeatable tests.
* Eclipse Public License - v 1.0

### [Mockito](http://site.mockito.org/)

* Tasty mocking framework for unit tests in Java
* MIT

### [Robolectric](http://robolectric.org/)

* Unit testing framework
* MIT

### [JodaTime](http://www.joda.org/joda-time/)

* A quality replacement for the Java date and time classes
* Required for unit tests
* Apache 2.0

## Contact

Contact us at [the Blockcerts community forum](http://community.blockcerts.org/).
