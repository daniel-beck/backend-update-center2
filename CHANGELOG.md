# Changelog

This changelog only covers the actual tool (`update-center2-x.y.z.jar`), not changes to the wrapper scripts or metadata.

## 3.4.6 (2021-03-01)

* In case of ambiguous releases (different groups, equivalent but different versions), consistently keep the older one. (#468)
* Rename `--whitelist-file` to `--allowed-artifacts-file` as part of terminology cleanup. (#482)

## 3.4.5 (2020-09-24)

* Add `target="_blank"` for all links in plugin descriptions. (#446)

## 3.4.4 (2020-09-22)

* Allow overriding the minimum validity duration using the Java system property `CERTIFICATE_MINIMUM_VALID_DAYS`. (#448, [INFRA-2732](https://issues.jenkins.io/browse/INFRA-2732))

## 3.4.3 (2020-09-10)

* When determining tiers, create a tier for the next weekly after an LTS baseline when there are tiers for that LTS line's releases. (#443)

## 3.4.2 (2020-08-22)

* Reduce the duration for which releases are considered _recent_ for `recent-releases.json` from 24 to 3 hours. (#433)

## 3.4.1 (2020-08-21)

* Also consider the Java system property `DOWNLOADS_ROOT_URL` for war download URLs, not just plugins. (#430)

## 3.4 (2020-08-21)

* Provide `recent-releases.json` to selectively sync new files to mirrors. (#428)
* Identify which plugins have invalid (non-existing) core dependencies. (#417)
* Close a `Reader`. (#413)
* Javadoc fixes. (#414)

## 3.3.1 (2020-08-01)

* Fix the URL we obtain the jar icon in generated directory listings from. (#409)
* Further standardize on `fastjson`, remove `gson`. (#394)

## 3.3 (2020-06-25)

* Dynamically determine update site tiers based on plugin dependencies. (#376, https://issues.jenkins.io/browse/INFRA-2615[INFRA-2615], [INFRA-1021](https://issues.jenkins.io/browse/INFRA-1021))

## 3.2.1 (2020-06-06)

* Fix XXE vulnerability when processing `pom.xml` files. (f207cfb0025017c9a525c57cdadb8416ee2d27c3)

## 3.2 (2020-05-29)

* Add information about what the latest version of a plugin is, even if not offered for installation. (#382, [JENKINS-62332](https://issues.jenkins.io/browse/JENKINS-62332))

## 3.1 (2020-05-26)

* Add separate, configurable `deprecations` metadata section to `update-center.json`. (#344, [JENKINS-59136](https://issues.jenkins.io/browse/JENKINS-59136))

## 3.0.1 (2020-05-23)

* Javadoc fixes. (fe8b8e09c20cddf578377cb0e9873e5604bd7a8d)

## 3.0 (2020-05-23)

* **Major Overhaul** (#365)
* Add mode that uses Artifactory API instead of Nexus Maven indexes (#364)
* Add plugin popularity metadata. (#356, #369)
* Use GitHub repository URL for plugins that do not specify a URL, or have an obviously wrong one. (#335)
* Prefer URL in plugin manifest to `url` in `pom.xml`. (#303, [INFRA-2292](https://issues.jenkins.io/browse/INFRA-2292)))

Version 3.0 is the first version that was not just recompiled by the wrapper script on every execution.
Before this release, this tool was essentially unversioned.
The changes listed above include all substantial changes to the tool since 2018.
