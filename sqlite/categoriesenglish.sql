-- phpMyAdmin SQL Dump
-- version 2.8.0.1
-- http://www.phpmyadmin.net
-- 
-- Host: custsql-nf13.eigbox.net
-- Generation Time: Oct 18, 2013 at 11:56 AM
-- Server version: 5.5.32
-- PHP Version: 4.4.9
-- 
-- Database: `cebuano`
-- 

-- --------------------------------------------------------

-- 
-- Table structure for table `categoriesenglish`
-- 

CREATE TABLE `categoriesenglish` (
  `catergoriesenglishid` int(11) NOT NULL AUTO_INCREMENT,
  `categoriesid` int(11) NOT NULL,
  `idenglish` int(11) NOT NULL,
  PRIMARY KEY (`catergoriesenglishid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin AUTO_INCREMENT=1 ;

-- 
-- Dumping data for table `categoriesenglish`
-- 

