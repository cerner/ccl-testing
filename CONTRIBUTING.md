# Contributing

Help us make this project better by contributing. Whether it is a new feature, a bug fix, or simply improving the documentation, your contribution is welcome. 
Please start by logging a [github issue][1] or by submitting a pull request.

To help ensure a smooth process for everyone, please read our [code of conduct][9] and review the following guidelines before contributing.


## Issue reporting

* Please browse [the existing issues][1] before logging a new issue.
* Check that the issue has not already been fixed in the `main` branch.
* Open an issue with a descriptive title and a summary.
* Please be as clear and explicit as you can in your description of the problem.
* Please state the affected versions in the description.
* Include any relevant code in the issue summary.

## Pull requests

* Read [how to properly contribute to open source projects on Github][2].
* Fork the project.
* Use a feature branch.
* Write [good commit messages][3].
* Use the same coding conventions as the rest of the project. 
  * Import these [eclipse preferences][eclipse_preferences] with [Workspace Mechanic][workspace_mechanic] to handle this automatically in [the eclipse IDE][eclipse].
* Commit locally and push to your fork until you are happy with your contribution.
* Make sure to add tests and verify all the tests are passing when merging upstream.
* Add an appropriate entry to the [Changelog][4].
* Please add your name to the [CONTRIBUTORS.md][8] file. Adding your name to the [CONTRIBUTORS.md][8] file signifies agreement to all rights and reservations provided by the [License][5].
* [Squash related commits together][6].
* Open a [pull request][7].
* The pull request will be reviewed by the community and merged by the project committers.

## Integration Tests
* An HNAM domain is required for integration testing.
* The integration tests for the CCL and Whitenoise pluigns require a profile providing the domain-specific settings and a system property named `maven-profile` 
with its value equal to the id of that profile.
The Whitenoise plugin further requires that profile to be activated by that system property. It is convenient to have the profile set the system property.
See [ccl-maven-plugin][10] and [whitenoise-maven-plugin][11] for details.

[eclipse]: https://eclipse.org
[eclipse_preferences]: ./ecosystem/eclipse/workspace_mechanic
[workspace_mechanic]: https://code.google.com/archive/a/eclipselabs.org/p/workspacemechanic 
[1]: https://github.com/cerner/ccl-testing/issues
[2]: http://gun.io/blog/how-to-github-fork-branch-and-pull-request
[3]: http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html
[4]: ./CHANGELOG.md
[5]: ./LICENSE.txt
[6]: http://gitready.com/advanced/2009/02/10/squashing-commits-with-rebase.html
[7]: https://help.github.com/articles/using-pull-requests
[8]: ./CONTRIBUTORS.md
[9]: ./CODE_OF_CONDUCT.md
[10]: ./ccl-maven-plugin/README.md
[11]: ./whitenoise/whitenoise-maven-plugin/README.md