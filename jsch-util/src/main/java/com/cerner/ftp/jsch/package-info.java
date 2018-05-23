/**
 * This package contains all objects that consumers need concern themselves with when using the jsch-utilities. In this
 * package:
 * <br>
 * <ul>
 * <li>{@link com.cerner.ftp.jsch.Connection}: a representative of a connection wrapping a JSch
 * {@link com.jcraft.jsch.Session} object.</li>
 * <li>{@link com.cerner.ftp.jsch.ConnectionPool}: a pool from which shared connection objects can be retrieved, most
 * notably to lessen time spent establish connections.</li>
 * </ul>
 */
package com.cerner.ftp.jsch;
