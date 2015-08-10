# VEFA Validator 2.0

Current version: **2.0.0-RC2**


## Features

* **Very easy to use.**
* **[Much](https://github.com/difi/vefa-validator/blob/master/doc/test_performance_001.md) [faster](https://github.com/difi/vefa-validator/blob/master/doc/test_performance_002.md)** than the old validator.
* Supports **rendering documents**.
* Very **low footprint** in your code.
* **Pooling** of resources.
* Supports **different lifecycles** of validation artifacts.


## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
	<groupId>no.difi.vefa</groupId>
	<artifactId>validator-core</artifactId>
	<version>2.0.0-RC2</version>
</dependency>
```

Start validating business documents:

```java
// Create a new validator using validation artifacts from Difi.
Validator validator = ValidatorBuilder.newValidator().build();

// Validate business document.
Validation validation = validator.validate(Paths.get("/path/to/document.xml"));

// Print result of validation.
System.out.println(validation.getReport().getFlag());
```

The validator is expensive to create, one instance should be enough.


## Validation artifacts

Artifacts may be found here:

* Production (current): [http://vefa.difi.no/validator/repo/](http://vefa.difi.no/validator/repo/)
* Test (upcoming): [https://test-vefa.difi.no/validator/repo/](https://test-vefa.difi.no/validator/repo/)

Difi does not guarantee the availability of the repositories containing validation artifacts. Local copy is always recommended for production environments.
