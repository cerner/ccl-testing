# Releasing ccl-testing

## Release Process

* If any of the maven plugins will be released 
    * Update their version in the [sample pom][sample-pom]
    * Update their versions in `src/main/resources/archetype-resource/pom.xml` of the impacted archetype projects.
    * Update their versions in their individual project readme files.
    * Include the impacted archtypes in the release.    
* Update the `<modules>` tag of the reactor pom files.
  * Add any new artifacts and uncomment any commented artifacts.
  * Add any new artifacts to the `site` profile.
* Ensure [changelog][changelog] is updated with the description of all changes being released and the date and versions are correct.
* Update [versions/release.txt][versions.release.txt] with
  * the snapshot version and release version for each artifact being released.
  * the snapshot version and previous release version for each artifact not being released.
  * commands for any new artifacts.
* Update [versions/snapshot.txt][versions.snapshot.txt] with
  * the new version and next snapshot version for each artifact being released.
  * commands for any new artifacts.
* Execute all the commands in [versions/release.txt][versions.release.txt].
  * Double check the version changes.
* Update the `<modules>` tag of the reactor pom files.
  * Comment any artifacts not being released.
* Commit the changes to a new branch.
* Determine the `release.number` (yyyy.mm.dd.N).
* Perform `mvn clean install site site:stage -P<profileId>,site -Drelease.number=<release.number>` on this branch. 
    * A final continuous integration test; `travis-ci` doesn't run the integration tests.
    * inspect the maven site to ensure there are no issues.
* Use `release.number` to create a tag for the release and merge the branch to master.
* Perform `mvn clean install deploy -P<profileId>,attach-artifacts,stage,ossrh -Dgpg.keyname=<keyname>` on the tag.
    * Use a jenkins job for this. 
    * Be sure to clear com.cerner.ccl and com.cerner.ftp from the local maven repo beforehand.
    * This assumes there is a profile with id ossrh specifying the gpg.passphrase
* Perform `mvn nexus-staging:release -Pstage -DstagingRepositoryId=<REPO>` on the tag.
    * The previous deploy command only staged the release in [sonatype][sonatype]. This command pushes them to maven central.
    * Determine REPO by finding the components on [sonatype][sonatype]. Most recently it was `Releases`.
    * NOTE: This command currently fails on the reactor pom, but all of the components do get released.

* Generate and deploy the maven site for the release.
    * `mvn clean site site:stage site-deploy -P<profileId>,site -Drelease.number=<release.number>`
* Update the `<modules>` tag of the reactor pom files.
  * Uncomment any artifacts not being released.
* Execute all the commands in [versions/snapshot.txt][versions.snapshot.txt]. 
  * Double check the version changes.
* Commit the changes to a new branch and merge to master.
## Release Prerequisites
* A valid Sonatype user account with write access to com.cerner is required to release this project. 
    * To create an account sign up with [Sonatype](https://issues.sonatype.org/secure/Signup!default.jspa).
* Deployed components must be signed. The verify phase of the the maven build will do this.
    * gpg must be installed and configured.  See [working with gpg][gpg help]. 
    * The gpg secrets and passwords must be encrypted and passed on to the jenkins box.
	```    
    $ export ENCRYPTION_PASSWORD=<password to encrypt>
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/secring.gpg -out .ci/secring.gpg.enc
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/pubring.gpg -out .ci/pubring.gpg.enc
	```

[changelog]:CHANGELOG.md
[gpg help]:https://central.sonatype.org/pages/working-with-pgp-signatures.html
[generating-ssh-keys]:https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/#generating-a-new-ssh-key
[versions.release.txt]:versions/release.txt
[versions.snapshot.txt]:versions/snapshot.txt
[sonatype]:https://oss.sonatype.org
[sample-pom]:ccl-maven-plugin/doc/SAMPLEPOM.md