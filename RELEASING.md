# Releasing ccl-testing

## Release Process

* If any of the maven plugins will be released 
    * Update their versions in `src/main/resources/archetype-resource/pom.xml` of the impacted archetype projects.
    * Include the impacted archtypes in the release.
* Update the `<modules>` tag of the reactor pom.
  * Add any new artifacts, comment out artifacts not being released, uncomment artifacts being released.
  * Be sure to update and release any artifact having a dependency that is being released.
* Ensure [changelog][changelog] is updated with the description of all changes being released and the date and versions are correct.
* Update [versions/release.txt][versions.release.txt] with the snapshot version and release version for each artifact being released.
  * Add commands for any new artifacts.
* Update [versions/snapshot.txt][versions.snapshot.txt] with the new version and next snapshot version for each artifact being released.
  * Add commands for any new artifacts.
* Execute all the commands in [versions/release.txt][versions.release.txt].
  * Double check the version changes.
* Commit the changes to a new branch.
* Perform `mvn clean install site -P<profileId>` on this branch as a final continuous integration test.
    * travis-ci does not execute the integration tests because they required an HNAM domain.
* Merge the branch to master and create a tag for the release.
* Perform `mvn clean install deploy` on the tag.
    * Use a jenkins job for this. 
    * Be sure to clear com.cerner.ccl and com.cerner.ftp from the local maven repo beforehand.
* Perform `mvn site site:deploy` on the tag. 
    * Use a jenkins job.
* Execute all the commands in [versions/snapshot.txt][versions.snapshot.txt]. 
  * Double check the version changes.
* Commit the changes to new branch and merge to master.

       
* The maven deploy pushes the artifact to the [sonatype staging repo](https://oss.sonatype.org/#stagingpositories).
* After the artifacts are deployed to the Sonatype staging repo, push them to maven central.
    * In Sonatype, scroll down to the latest ccl-testing repo from the list. 
    * Click on the release button to push the artifact to maven central.
      * it takes roughly 2 hours for the artifacts to sync with maven central.

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
