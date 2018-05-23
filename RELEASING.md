# Releasing ccl-testing

If the [Release Prerequisites](#release-prerequisites) have already been performed you can skip to the [Release Process](#release-process) section.

## Release Prerequisites

* A valid Sonatype user account is required to release this project. To create an account sign up with [Sonatype](https://issues.sonatype.org/secure/Signup!default.jspa).

* Install `gpg2` - This tool is used to automatically sign off artifacts
	* Follow this [guide](http://central.sonatype.org/pages/working-with-pgp-signatures.html#generating-a-key-pair) to generate your own gpg key and secret.
	* Choose a password to encrypt the public and private keys that were generated in the previous step using gpg2. Execute the below steps to encrypt the keys.
	
	```    
    $ export ENCRYPTION_PASSWORD=<password to encrypt>
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/secring.gpg -out .ci/secring.gpg.enc
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/pubring.gpg -out .ci/pubring.gpg.enc
	```
* All the secrets and passwords must be encrypted and passed on to the continuous integration system.
 
* Create a new set of ssh keys to push the documentation site to `gh-pages` branch. Follow this github [documentation](https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/#generating-a-new-ssh-key) to create the ssh keys.
	* **Note**: The ssh keys file names has to be `deploy_site_key`.
	* Before generating the keys make sure the current directory is the directory where all your ssh keys are stored. By default this would be `~/.ssh`
* Add the contents of `deploy_site_key.pub` to the [ccl-testing deploy keys](https://github.com/cerner/ccl-testing/settings/keys).
	* *pro tip*: You can copy the contents using `pbcopy < path/to/deploy_site_key.pub`
* Encrypt the `deploy_site_key` key and add it to the continuous integration system.

	```
	$ cd path/to/ccl-testing
	$ ci encrypt-file ~/.ssh/deploy_site --add
	``` 
* Commit all the changes to the ccl-testing repo.
 
## Release Process

* Update the `<modules>` tag of the reactor pom to include the components to be included in the release.
* Update [the changelog][changelog] to have a description of the updates for each component in the release. Indicate the version number for each.
* Commit the changes.
* Clean up any previous release backup/release property files with the following command.

    ```
    mvn release:clean
    ```
* Prepare the project for releasing with the following command.

    ```
    mvn clean release:prepare
    ```
    * Maven will prompt for the release version and next devlopment cycle version for the reactor pom and each component listed in the reactor pom. 
    The release versions should match the versions indicated in the changelog.
    * Maven then performs the reactor build. If the build succeeds, maven updates the versions of the reactor pom and each component then pushes the following items 
to the git repo
        * a commit for the release
        * a commit for the next development cycle
        * the tag that was cut for the release
    * **Note**: 
        * Accounts must have [two factor authentication](https://help.github.com/articles/about-two-factor-authentication/) enabled to release. 
        Because of this we need to generate a [personal access token](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/) 
        to use in lieu of a password during the release.
        * If at anytime the release needs to be stopped. Cancel the maven commands using (ctrl + c) and run the below command 
        
        ```
        mvn release:rollback
        ```
        
* The continuous integration system starts a new build for the released tag and pushes the artifact to [sonatype staging repo](https://oss.sonatype.org/#stagingpositories).
* Once the artifacts are pushed to the Sonatype staging repo
    * Scroll down to the latest ccl-testing repo from the list. 
    * click on the release button to push the artifact to maven central.
    * **Note**: It takes roughly 2 hours for the artifacts to sync with the maven central.
* Builds the documentation site for the released tag and publishes it to `gh-pages`.
* The continuous integration system starts another build for the current snapshot and pushes the artifacts to [sonatype snapshots repo](https://oss.sonatype.org/content/repositories/snapshots/com/cerner/ccl-testing/).


[changelog]:CHANGELOG.md