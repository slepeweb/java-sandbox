-- MySQL dump 10.13  Distrib 8.0.31, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: slepeweb_cms
-- ------------------------------------------------------
-- Server version	8.0.31-0ubuntu0.20.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `access`
--

DROP TABLE IF EXISTS `access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access` (
  `id` int NOT NULL AUTO_INCREMENT,
  `siteid` int NOT NULL,
  `mode` varchar(1) DEFAULT 'r',
  `name` varchar(64) DEFAULT NULL,
  `tag` varchar(64) DEFAULT NULL,
  `template` varchar(64) DEFAULT NULL,
  `path` varchar(64) DEFAULT NULL,
  `ownerid` varchar(64) DEFAULT NULL,
  `role` varchar(64) DEFAULT NULL,
  `access` tinyint(1) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_access_siteid_mode_name` (`siteid`,`mode`,`name`),
  KEY `idx_enabled` (`enabled`)
) /*!50100 TABLESPACE `innodb_system` */ ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `axis`
--

DROP TABLE IF EXISTS `axis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `axis` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shortname` varchar(16) NOT NULL,
  `label` varchar(32) NOT NULL,
  `units` varchar(16) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_axis_shortname` (`shortname`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `axisvalue`
--

DROP TABLE IF EXISTS `axisvalue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `axisvalue` (
  `id` int NOT NULL AUTO_INCREMENT,
  `axisid` int DEFAULT NULL,
  `value` varchar(64) NOT NULL,
  `ordering` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_axisvalue_value` (`value`),
  KEY `axisid` (`axisid`),
  CONSTRAINT `axisvalue_ibfk_1` FOREIGN KEY (`axisid`) REFERENCES `axis` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config` (
  `siteid` int NOT NULL DEFAULT '0',
  `name` varchar(128) NOT NULL DEFAULT '',
  `value` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`siteid`,`name`),
  CONSTRAINT `config_ibfk_1` FOREIGN KEY (`siteid`) REFERENCES `site` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `field`
--

DROP TABLE IF EXISTS `field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `field` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `variable` varchar(32) NOT NULL,
  `multilingual` tinyint(1) DEFAULT '0',
  `fieldtype` enum('text','markup','integer','date','url','radio','checkbox','select','datetime','dateish','layout') DEFAULT NULL,
  `size` int NOT NULL,
  `helptext` varchar(512) DEFAULT NULL,
  `dflt` varchar(64) DEFAULT NULL,
  `valid` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_field_variable` (`variable`)
) /*!50100 TABLESPACE `innodb_system` */ ENGINE=InnoDB AUTO_INCREMENT=822 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fieldfortype`
--

DROP TABLE IF EXISTS `fieldfortype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fieldfortype` (
  `fieldid` int NOT NULL DEFAULT '0',
  `itemtypeid` int NOT NULL DEFAULT '0',
  `fieldorder` int NOT NULL,
  `mandatory` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`fieldid`,`itemtypeid`),
  KEY `itemtypeid` (`itemtypeid`),
  CONSTRAINT `fieldfortype_ibfk_1` FOREIGN KEY (`fieldid`) REFERENCES `field` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fieldfortype_ibfk_2` FOREIGN KEY (`itemtypeid`) REFERENCES `itemtype` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fieldvalue`
--

DROP TABLE IF EXISTS `fieldvalue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fieldvalue` (
  `fieldid` int NOT NULL DEFAULT '0',
  `itemid` int NOT NULL DEFAULT '0',
  `stringvalue` mediumtext,
  `integervalue` int DEFAULT NULL,
  `datevalue` timestamp NULL DEFAULT NULL,
  `language` varchar(2) NOT NULL DEFAULT '',
  PRIMARY KEY (`itemid`,`fieldid`,`language`),
  KEY `fieldid` (`fieldid`),
  CONSTRAINT `fieldvalue_ibfk_1` FOREIGN KEY (`fieldid`) REFERENCES `field` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fieldvalue_ibfk_2` FOREIGN KEY (`itemid`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `host`
--

DROP TABLE IF EXISTS `host`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `host` (
  `id` int NOT NULL AUTO_INCREMENT,
  `siteid` int DEFAULT NULL,
  `protocol` varchar(8) DEFAULT 'http',
  `name` varchar(255) DEFAULT NULL,
  `port` int DEFAULT '8080',
  `type` enum('editorial','delivery','both') DEFAULT 'both',
  `deployment` enum('development','production') DEFAULT 'development',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_host_name_port_type` (`name`,`port`,`type`),
  UNIQUE KEY `idx_site_type_deployment` (`siteid`,`type`,`deployment`),
  KEY `siteid` (`siteid`),
  CONSTRAINT `host_ibfk_1` FOREIGN KEY (`siteid`) REFERENCES `site` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) /*!50100 TABLESPACE `innodb_system` */ ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `origid` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `simplename` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `siteid` int DEFAULT NULL,
  `typeid` int DEFAULT NULL,
  `templateid` int DEFAULT NULL,
  `ownerid` int DEFAULT NULL,
  `datecreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dateupdated` timestamp(3) NULL DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT NULL,
  `editable` tinyint(1) DEFAULT NULL,
  `published` tinyint(1) DEFAULT NULL,
  `searchable` tinyint(1) DEFAULT NULL,
  `version` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_item_site_path_version` (`siteid`,`path`,`version`),
  KEY `typeid` (`typeid`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_editable` (`editable`),
  KEY `idx_published` (`published`),
  KEY `idx_origid` (`origid`),
  KEY `idx_siteid_ownerid` (`siteid`,`ownerid`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`siteid`) REFERENCES `site` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `item_ibfk_2` FOREIGN KEY (`typeid`) REFERENCES `itemtype` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4138 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itemtype`
--

DROP TABLE IF EXISTS `itemtype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itemtype` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `mimetype` varchar(64) DEFAULT NULL,
  `privatecache` int DEFAULT NULL,
  `publiccache` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_itemtype_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=964 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `link`
--

DROP TABLE IF EXISTS `link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `link` (
  `parentid` int NOT NULL DEFAULT '0',
  `childid` int NOT NULL DEFAULT '0',
  `linktypeid` int DEFAULT NULL,
  `linknameid` int DEFAULT NULL,
  `ordering` smallint DEFAULT NULL,
  `data` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`parentid`,`childid`),
  KEY `idx_link_child` (`childid`),
  KEY `linktypeid` (`linktypeid`),
  KEY `linknameid` (`linknameid`),
  CONSTRAINT `link_ibfk_1` FOREIGN KEY (`parentid`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `link_ibfk_2` FOREIGN KEY (`childid`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `link_ibfk_3` FOREIGN KEY (`linktypeid`) REFERENCES `linktype` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `link_ibfk_4` FOREIGN KEY (`linknameid`) REFERENCES `linkname` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linkname`
--

DROP TABLE IF EXISTS `linkname`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linkname` (
  `id` int NOT NULL AUTO_INCREMENT,
  `siteid` int DEFAULT NULL,
  `linktypeid` int DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_linkname_site_type_name` (`siteid`,`linktypeid`,`name`),
  KEY `linktypeid` (`linktypeid`),
  CONSTRAINT `linkname_ibfk_1` FOREIGN KEY (`siteid`) REFERENCES `site` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `linkname_ibfk_2` FOREIGN KEY (`linktypeid`) REFERENCES `linktype` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) /*!50100 TABLESPACE `innodb_system` */ ENGINE=InnoDB AUTO_INCREMENT=875 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linktype`
--

DROP TABLE IF EXISTS `linktype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linktype` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_linktype_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `loglevel`
--

DROP TABLE IF EXISTS `loglevel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loglevel` (
  `package` varchar(512) NOT NULL DEFAULT '',
  `level` enum('TRACE','DEBUG','INFO','WARN','ERROR') DEFAULT NULL,
  PRIMARY KEY (`package`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `media`
--

DROP TABLE IF EXISTS `media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media` (
  `itemid` int NOT NULL DEFAULT '0',
  `folder` varchar(4) DEFAULT NULL,
  `thumbnail` tinyint(1) NOT NULL DEFAULT '0',
  `size` int DEFAULT NULL,
  PRIMARY KEY (`itemid`,`thumbnail`),
  CONSTRAINT `media_ibfk_1` FOREIGN KEY (`itemid`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) /*!50100 TABLESPACE `innodb_system` */ ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `origitemid` int NOT NULL DEFAULT '0',
  `siteid` int DEFAULT NULL,
  `partnum` varchar(256) NOT NULL,
  `stock` int DEFAULT NULL,
  `price` int DEFAULT NULL,
  `alphaaxisid` int DEFAULT NULL,
  `betaaxisid` int DEFAULT NULL,
  PRIMARY KEY (`origitemid`),
  UNIQUE KEY `idx_product_partnum` (`siteid`,`partnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `userid` int NOT NULL,
  `siteid` int NOT NULL,
  `role` varchar(64) NOT NULL,
  PRIMARY KEY (`userid`,`siteid`,`role`),
  KEY `siteid` (`siteid`),
  CONSTRAINT `role_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `role_ibfk_2` FOREIGN KEY (`siteid`) REFERENCES `site` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `site`
--

DROP TABLE IF EXISTS `site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `site` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `shortname` varchar(8) DEFAULT NULL,
  `language` varchar(2) DEFAULT NULL,
  `xlanguages` varchar(16) DEFAULT NULL,
  `secured` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_site_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=144 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sitetype`
--

DROP TABLE IF EXISTS `sitetype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sitetype` (
  `siteid` int NOT NULL DEFAULT '0',
  `typeid` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`siteid`,`typeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `siteid` int DEFAULT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`itemid`,`value`),
  KEY `idx_tag_site_value` (`siteid`,`value`),
  CONSTRAINT `tag_ibfk_1` FOREIGN KEY (`itemid`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `template` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `forward` varchar(255) DEFAULT NULL,
  `siteid` int DEFAULT NULL,
  `typeid` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_template_site_name` (`siteid`,`name`),
  KEY `typeid` (`typeid`),
  CONSTRAINT `template_ibfk_1` FOREIGN KEY (`siteid`) REFERENCES `site` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `template_ibfk_2` FOREIGN KEY (`typeid`) REFERENCES `itemtype` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstname` varchar(128) NOT NULL,
  `lastname` varchar(128) NOT NULL,
  `email` varchar(128) NOT NULL,
  `phone` varchar(24) NOT NULL,
  `password` varchar(256) DEFAULT NULL,
  `editor` tinyint(1) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  `secret` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_email` (`email`),
  KEY `idx_user_secret` (`secret`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `variant`
--

DROP TABLE IF EXISTS `variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variant` (
  `origitemid` int NOT NULL DEFAULT '0',
  `qualifier` varchar(32) NOT NULL,
  `stock` int DEFAULT NULL,
  `price` int DEFAULT NULL,
  `alphavalueid` int NOT NULL DEFAULT '0',
  `betavalueid` int NOT NULL DEFAULT '-1',
  PRIMARY KEY (`origitemid`,`alphavalueid`,`betavalueid`),
  UNIQUE KEY `idx_variant_unique` (`origitemid`,`qualifier`),
  CONSTRAINT `variant_ibfk_1` FOREIGN KEY (`origitemid`) REFERENCES `product` (`origitemid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-10-02 14:29:36
