-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: foodorder
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `foodorder`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `foodorder` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `foodorder`;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'特色海鲜','/image/category_haixian.png'),(2,'美味小炒','/image/category_chaocai.png'),(3,'酒水饮料','/image/category_jiushui.png'),(4,'全部商家','/image/category_sahngjia.png'),(5,'推荐','/image/category_recommond.png');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dishes`
--

DROP TABLE IF EXISTS `dishes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dishes` (
  `dish_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `category` varchar(50) NOT NULL,
  `merchant_id` int NOT NULL,
  `browse_count` int NOT NULL DEFAULT '0',
  `description` text,
  `sales` int NOT NULL DEFAULT '0',
  `stock` int NOT NULL DEFAULT '0',
  `specifications` json DEFAULT NULL,
  `merchant_name` varchar(255) NOT NULL,
  PRIMARY KEY (`dish_id`),
  KEY `merchant_id` (`merchant_id`),
  CONSTRAINT `dishes_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dishes`
--

LOCK TABLES `dishes` WRITE;
/*!40000 ALTER TABLE `dishes` DISABLE KEYS */;
INSERT INTO `dishes` VALUES (1,'老友米线',6.00,'haixian_shousi.jpg','小吃',1,1,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','意大利面馆'),(2,'老友粉',6.00,'haixian_shousi.jpg','小吃',1,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','意大利面馆'),(3,'牛腩粉',7.00,'haixian_shousi.jpg','小吃',1,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','意大利面馆'),(4,'牛腩面',7.00,'haixian_shousi.jpg','小吃',2,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','藤原寿司店'),(5,'淮山面',7.00,'chaocai_huiguorou.jpg','小吃',2,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','藤原寿司店'),(6,'三鲜老友粉',6.00,'chaocai_huiguorou.jpg','小吃',2,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','藤原寿司店'),(7,'三鲜老友面',6.00,'chaocai_xiangganhuiguorou.jp','小吃',2,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','藤原寿司店'),(8,'猪杂老友面',6.00,'chaocai_xiangganhuiguorou.jp','小吃',12,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','好再来川菜馆'),(9,'猪杂老友粉',6.00,'chaocai_xiangganhuiguorou.jp','小吃',3,0,NULL,0,0,'{\"份量\": [\"小份\", \"中份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','好再来川菜馆'),(10,'螺蛳粉',6.00,'chaocai_xiangganhuiguorou.jp','小吃',11,0,NULL,0,0,'{\"份量\": [\"一两\", \"二两\", \"三两\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"], \"口味\": [\"不辣\", \"微辣\", \"中辣\", \"特辣\"]}','好再来川菜馆'),(11,'干捞桂林米粉',8.00,'chaocai_xiangganhuiguorou.jp','小吃',3,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','好再来川菜馆'),(12,'干捞螺蛳粉',8.00,'chaocai_xiangganhuiguorou.jp','小吃',3,0,NULL,0,0,'{\"份量\": [\"一两\", \"二两\", \"三两\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"], \"口味\": [\"不辣\", \"微辣\", \"中辣\", \"特辣\"]}','好再来川菜馆'),(13,'锅烧桂林米粉',8.00,'chaocai_xiangganhuiguorou.jpg','小吃',3,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','好再来川菜馆'),(14,'叉烧桂林米粉',23.00,'content://media/external/images/media/400','小吃',13,0,'超级好吃的',0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','好再来川菜馆'),(15,'农夫山泉矿泉水',2.00,'chaocai_xiangganhuiguorou.jpg','甜品饮料',13,0,NULL,0,0,'{\"份量\": [\"中瓶\", \"1.5升\"]}','好吃的湘菜馆'),(16,'炒桂林米粉',9.00,'chaocai_xiangganhuiguorou.jpg','粉',7,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(17,'蛋花瘦肉粉',7.50,'chaocai_xiangganhuiguorou.jpg','粉',7,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(18,' 花甲粉',8.00,'jiushui_natie.jpg','粉',6,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(19,'鸡扒河粉',7.00,'jiushui_natie.jpg','粉',6,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(20,'三鲜螺蛳粉',8.00,'jiushui_natie.jpg','粉',6,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"], \"口味\": [\"不辣\", \"微辣\", \"中辣\", \"特辣\"]}','霸王茶姬'),(21,'酸菜瘦肉粉',7.00,'jiushui_natie.jpg','粉',6,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(22,'酸辣珍珠粉',8.00,'jiushui_natie.jpg','粉',7,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','茶百道'),(23,'无骨鱼片粉',9.00,'jiushui_fengmiyouzi.jpg','粉',7,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','茶百道'),(24,'香辣牛杂粉',8.00,'jiushui_fengmiyouzi.jpg','粉',8,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','茶百道'),(25,'鱼丸豆腐粉',7.00,'jiushui_fengmiyouzi.jpg','粉',8,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','茶百道'),(26,' 重庆小面',8.00,'jiushui_fengmiyouzi.jpg','面',10,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(27,'蛋花瘦肉面',7.50,'jiushui_fengmiyouzi.jpg','面',6,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(28,'酸辣干拌面',8.00,'jiushui_fengmiyouzi.jpg','面',9,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(29,'炸酱面',7.00,'jiushui_baitaowulong.jpg','面',6,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"], \"加菜\": [\"油豆腐\", \"煎蛋\", \"腐竹\", \"鸡爪\", \"卤蛋\", \"腊肠\", \"炸蛋\", \"热狗肠\"]}','霸王茶姬'),(30,'腊味炒饭',9.00,'jiushui_baitaowulong.jpg','美味小炒',13,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"]}','霸王茶姬'),(32,'扬州炒饭',9.00,'jiushui_baitaowulong.jpg','美味小炒',6,0,NULL,0,0,'{\"份量\": [\"小份\", \"大份\"]}','霸王茶姬'),(35,'你',12.00,'content://media/external/images/media/420','好',13,0,'门后面没有记得吗',0,0,NULL,'北京烤鸭档口');
/*!40000 ALTER TABLE `dishes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchants`
--

DROP TABLE IF EXISTS `merchants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchants` (
  `merchant_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `window_location` varchar(255) NOT NULL,
  `business_hours` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `browse_count` int NOT NULL,
  `image_name` varchar(100) NOT NULL DEFAULT 'default_merchant',
  `rating` decimal(2,1) NOT NULL DEFAULT '4.5',
  `sales` int NOT NULL DEFAULT '0',
  `min_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `discount_info` varchar(200) DEFAULT NULL,
  `delivery` tinyint(1) NOT NULL DEFAULT '1',
  `remark` varchar(500) DEFAULT NULL,
  `category` varchar(50) NOT NULL DEFAULT '南食堂',
  `password` varchar(50) NOT NULL DEFAULT '123456',
  PRIMARY KEY (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchants`
--

LOCK TABLES `merchants` WRITE;
/*!40000 ALTER TABLE `merchants` DISABLE KEYS */;
INSERT INTO `merchants` VALUES (1,'意大利面馆','窗口1','10:00 - 23:00','意大利面已成为世界的宠儿，‌其影响力遍布全球，‌从家庭餐桌到国际空间站，‌再到各种文化交流活动，‌意大利面和沙拉作为欧洲的特色美食，‌代表着欧洲烹饪的精髓，‌是许多家庭餐桌上的佳肴。‌\r\n\r\n意大利面的种类繁多，‌形状各异，‌包括直身粉、‌螺丝型、‌弯管型、‌蝴蝶型、‌空心型、‌贝壳型等，‌据说至少有500种不同的种类。‌这些多样的形状和种类使得意大利面可以搭配上千种不同的酱汁，‌成为意大利的特色主食。‌意大利面不仅在意大利本土受到喜爱，‌也受到了全球的欢迎，‌成为世界各地餐厅和家庭餐桌上的常见食物。‌',532,'merchants_bawangchaji',4.5,740,99.42,'8折',1,'新品','北商业街','123'),(2,'藤原寿司店','窗口2','11:00 - 21:00','寿司是日本的传统美食,一般选用新鲜的鱼、虾肉等材料,经过腌制后再放进油锅中煎炸,然后捞出来撒上调味料食用。 复制 3. 寿司,是日本传入我国的食品之一,已有二千多年历史',113,'merchants_tenyuanshousi',4.5,593,23.19,'9折',1,NULL,'免辣','123456'),(3,'好再来川菜馆','窗口3','11:00 - 21:00','感受川菜的麻辣鲜香，让您的味蕾迸发出无与伦比的激情！穿越千里川江，只为呈上一道地道的川菜。川菜馆，让您品尝到四川的味道。热爱川菜的朋友们，回锅肉将是您的最佳选择。在四川，这道菜被誉为“川菜之魂”。肉质鲜嫩、辣香四溢，搭配上四川特有的豆瓣酱和蒜苗，让您仿佛置身于成都的小巷中，品尝着最地道的川菜。',502,'merchants_chabaidao',4.5,826,46.39,'7折',1,'新品','北商业街','12'),(4,'好吃的湘菜馆','窗口4','11:00 - 21:00','这家湘菜馆位于市中心的繁华地段，环境宜人，装修简约大方，营造出一种舒适的用餐氛围。店内的服务人员热情周到，为顾客提供贴心的服务，使得顾客能够尽情享受美食。在这家湘菜馆，你可以品尝到各种地道的湘菜。其中，辣椒炒肉、蒸鱼头、麻婆豆腐等都是必尝之选。每一道菜都采用新鲜的食材，经过精心制作，将湘菜的独特风味展现得淋漓尽致。此外，馆内的菜品丰富多样，无论你是喜欢辣还是喜欢清淡，都能在这里找到适合自己口味的菜品。',5001,'merchants_bawangchaji',4.5,295,83.50,'9折',1,'新品','免辣','123456'),(6,'霸王茶姬','窗口5','11:00 - 21:00','在霸王茶姬的奶茶中，找到了一种与众不同的味觉体验，让我们一起沉醉于这个美妙的世界。走进霸王茶姬，品味自然的味道，让心灵沐浴在绿色的海洋！欢迎来到霸王茶姬，我们致力于为您呈现最优质的茶饮体验。霸王茶姬，以茶会友，共享茶趣。我们精选上等茶叶，结合传统工艺与现代科技，为您带来最纯正的茶香与最完美的口感。我们的产品以独特的口感和优质的原料脱颖而出。每一杯霸王茶姬都融合了茶叶的醇厚与清新，让您在品尝之余，亦能感受到茶叶所蕴含的大自然之美。选用上乘茶叶，严格控制品质，我们的茶饮无论是口感还是品质，都为您精心呈现。霸王茶姬，不仅仅是一款茶饮，更是一种生活方式。我们秉承“以茶会友”的品牌理念，希望通过一杯茶，连接人与人之间的情感，分享生活中的美好。在这里，您将品味到那份与众不同的宁静与优雅，感受到霸王茶姬所代表的独特文化。那么，何不尝试一下霸王茶姬呢？购买我们的产品，让您的味蕾沉浸在茶香之中，体验一份与众不同的美好。或者，您可以参加我们的品茶活动，与志同道合的茶友一起分享品茶的快乐。最后，请注意：霸王茶姬的茶叶营养丰富，但不适合隔夜饮用。此外，孕妇、哺乳期妇女及小孩不宜饮用。请您在享用霸王茶姬时，按照产品说明进行操作，确保饮用安全。让我们一同品味霸王茶姬带来的美好时光！',523,'default_merchant',4.5,287,97.71,NULL,1,NULL,'免辣','123456'),(7,'茶百道','窗口6','11:00 - 21:00','茶百道，精选上等茶叶，以匠心独运的手法冲泡而成。每一杯茶都是对品质生活的执着追求，让你在忙碌中感受一份惬意。茶百道，用心做好每一杯茶。我们精选上等茶叶，严格把控每一个制作环节，只为让每一杯茶都散发出诱人的香气和醇厚的口感。',457,'default_merchant',4.5,774,27.18,NULL,1,'推荐','北商业街','123456'),(8,'蜜雪冰城','窗口8','09:00 - 21:00','蜜雪冰城，作为冰淇淋行业的佼佼者，我们拥有众多独特的口味和新鲜的原材料。从香草、巧克力到水果系列，每一款冰淇淋都是经过精心研制，绝对让你回味无穷！蜜雪冰城的产品优势在于其采用新鲜的水果和牛奶，以及特殊的制作工艺，使得冰淇淋和茶饮的口感更加美味、健康。此外，蜜雪冰城的价格合理，受到消费者的欢迎。',113,'merchants_mixuebingcheng',4.5,310,52.72,'8折',1,'新品','北商业街','123456'),(9,'金韩宫','窗口10','14:00 - 21:00','金韩宫是一家专注于提供正宗韩国料理的餐馆，‌其特色在于将传统的炭火烤肉改良为不受烟熏火燎的新食法。‌金韩宫采用上乘的滋补调味品，‌选用牛、‌羊肉来自拥有雄厚技术实力的现代化大型企业，‌确保肉品的质量。‌整个加工过程严格按照国际HACCP体系要求，‌进行低温排酸及速冻保鲜，‌全程封闭、‌无菌，‌使得肉品具有鲜嫩爽滑、‌香浓适口、‌回味悠长的特点。‌\r\n\r\n金韩宫的主营项目以烤肉为主，‌提供搭配合理的各类套餐及精心制作的各类菜品，‌包括五花肉、‌牛肉、‌海鲜等，‌配合新鲜的蔬菜、‌味道鲜美的点心和温和的韩国酒类。‌客人可以自行烤制享受烹饪的乐趣，‌也可以选择由服务员全程一对一服务，‌充分体现至尊享受。‌',8220,'merchants_jinhangong',4.5,65,60.22,'7折',1,'推荐','免辣','123456'),(10,'农家菜馆','窗口11','10:00 - 21:00','农家菜馆的烹饪技艺高超，让人对美食有了新的认识。这里的每一道菜都充满了独特的味道，让人难以忘怀。乡村风味：农家菜有着浓郁的乡村风味，让人仿佛置身于田园风光之中。其食材新鲜、天然，让人吃着放心。而且，农家菜肴注重原汁原味，更能带出食材本身的鲜美。',790,'merchants_nongjiacaiguan',4.5,553,16.40,NULL,1,'推荐','北食堂','123456'),(11,'四川麻辣香锅','窗口2','09:00 - 21:00','源自川蜀的麻辣香锅，集辣香于一体，口味丰富多变。',350,'default_merchant',4.2,120,15.00,'满50减5',1,'招牌香辣推荐','南食堂','123456'),(12,'兰州牛肉拉面','窗口3','07:30 - 20:30','正宗清汤牛肉拉面，汤鲜味美，筋道爽滑。',480,'default_merchant',4.5,260,12.00,'加面免费',1,'人气爆款','南食堂','123456'),(13,'北京烤鸭档口','窗口4','10:00 - 22:00','精选北京填鸭，脆皮多汁，是经典的北京风味美食。',520,'merchants_beijingkaoya',4.6,180,58.00,'午市折扣9折',1,'北京老字号','南食堂','123456'),(14,'粤式烧腊饭','窗口5','09:00 - 21:00','秘制烧腊搭配爽口青菜，配上特制酱汁，广式风味十足。',275,'merchants_yueshishaola',4.3,90,20.00,'满30减3',1,'烧腊拼盘特惠','北食堂','123456'),(15,'韩国拌饭','窗口6','08:00 - 19:00','石锅拌饭、泡菜、韩式烤肉多种组合，异国风味诱人。',312,'merchants_hanguobanfan',4.4,150,25.00,'满100送饮料',1,'石锅必点','南商业街','123456'),(16,'东北饺子馆','窗口7','08:30 - 20:00','皮薄馅大，口感鲜美，多种馅料可选，地道东北风味。',298,'merchants_dongbeijiaozi',4.7,130,10.00,'买二两送一两',1,'手工水饺','南商业街','123456'),(17,'港式茶餐厅','窗口8','07:00 - 19:30','港式风味，奶茶、菠萝包、叉烧饭，带你领略香港美食。',615,'merchants_gangshichacan',4.5,220,18.00,'下午茶时段9折',1,'丝袜奶茶必试','免辣','123456'),(18,'新疆大盘鸡','窗口9','10:00 - 20:00','香辣浓郁、肉质鲜嫩，配以宽面或米饭，更具风味。',445,'merchants_xinjiangdapanji',4.4,170,28.00,'满80减10',1,'推荐加面','北商业街','123456'),(19,'日式寿司便当','窗口10','09:30 - 20:30','每日新鲜食材，精致寿司与便当，口感清爽。',380,'merchants_rishishousi',4.8,140,35.00,'第二份半价',1,'刺身限量','北商业街','123456'),(20,'意大利面馆','窗口11','10:00 - 23:00','意大利面如今风靡全球，从家庭餐桌到国际美食盛宴，都少不了它的身影。',532,'merchants_yidalimian',4.5,740,99.42,'8折',1,'新品','免辣','123456'),(21,'00','00','00','新店开业，欢迎光临！',0,'default_merchant',4.5,0,0.00,NULL,1,NULL,'00','00'),(22,'1','1','11','新店开业，欢迎光临！',0,'default_merchant',4.5,0,0.00,NULL,1,NULL,'11','1');
/*!40000 ALTER TABLE `merchants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderrecords`
--

DROP TABLE IF EXISTS `orderrecords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderrecords` (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `merchant_id` int NOT NULL,
  `dish_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `status` varchar(20) NOT NULL DEFAULT 'cart',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `student_id` (`student_id`),
  KEY `merchant_id` (`merchant_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `orderrecords_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `orderrecords_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`),
  CONSTRAINT `orderrecords_ibfk_3` FOREIGN KEY (`dish_id`) REFERENCES `dishes` (`dish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderrecords`
--

LOCK TABLES `orderrecords` WRITE;
/*!40000 ALTER TABLE `orderrecords` DISABLE KEYS */;
INSERT INTO `orderrecords` VALUES (12,1,1,3,1,'cart','2025-03-19 10:24:30','2025-03-19 10:24:30');
/*!40000 ALTER TABLE `orderrecords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `order_time` datetime NOT NULL,
  `student_id` int NOT NULL,
  `merchant_id` int NOT NULL,
  `dish_list` text NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `order_status` varchar(20) NOT NULL,
  `dish_id` int NOT NULL,
  `dining_option` varchar(20) NOT NULL,
  `order_quantity` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `student_id` (`student_id`),
  KEY `merchant_id` (`merchant_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`dish_id`) REFERENCES `dishes` (`dish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2024-01-10 12:30:00',1,1,'1:2,2:1',45.00,'completed',1,'delivery',3,'2025-03-19 10:19:47','2025-03-19 03:07:21'),(2,'2024-01-10 13:15:00',2,1,'2:1,3:2',68.00,'paid',2,'pickup',3,'2025-03-17 10:19:47','2025-03-19 03:07:29'),(3,'2024-01-10 14:00:00',1,1,'4:1',25.00,'paid',4,'delivery',1,'2025-01-06 10:19:47','2025-03-19 03:18:02'),(4,'2024-01-10 15:30:00',3,1,'5:2,6:1',88.00,'completed',5,'delivery',3,'2025-01-06 10:19:47','2025-03-19 03:18:07'),(5,'2024-01-10 16:45:00',2,2,'7:1,8:2',56.00,'cancelled',7,'pickup',3,'2025-01-06 10:19:47','2025-01-06 10:19:47'),(9,'2025-01-06 18:26:02',1,13,'14:8,30:3',91.00,'completed',30,'delivery',1,'2025-01-06 10:26:01','2025-01-07 06:36:20'),(10,'2025-01-01 11:45:10',1,13,'15:2,14:2',51.00,'completed',30,'delivery',1,'2025-01-06 10:45:09','2025-01-07 06:36:17'),(11,'2025-01-05 11:39:13',4,13,'14:2,30:3',53.00,'completed',14,'pickup',2,'2025-01-07 04:56:58','2025-01-07 06:03:16'),(12,'2025-01-04 15:42:13',2,13,'14:1',13.00,'completed',14,'pickup',1,'2025-01-07 04:58:19','2025-01-07 05:44:40'),(13,'2025-01-02 17:46:13',4,13,'15:2,14:2',44.00,'completed',30,'pickup',2,'2025-01-07 04:58:36','2025-01-07 05:43:21'),(14,'2025-01-03 18:12:13',3,13,'31:1,30:3',40.00,'completed',14,'pickup',1,'2025-01-07 04:59:04','2025-01-07 05:43:49'),(15,'2025-01-07 06:55:04',1,13,'14:1,30:2,15:1',33.00,'paid',15,'delivery',0,'2025-01-07 07:34:59','2025-01-07 07:35:16');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `dish_id` int NOT NULL,
  `merchant_id` int NOT NULL,
  `content` text NOT NULL,
  `rating` tinyint NOT NULL,
  `review_time` datetime NOT NULL,
  PRIMARY KEY (`review_id`),
  KEY `student_id` (`student_id`),
  KEY `dish_id` (`dish_id`),
  KEY `merchant_id` (`merchant_id`),
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`dish_id`) REFERENCES `dishes` (`dish_id`),
  CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`),
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (2,2,2,2,'寿司非常美味！',4,'2024-07-24 13:15:00'),(3,1,2,2,'寿司非常美味！寿司非常美味！寿司非常美味！',4,'2024-07-25 11:12:46'),(4,3,2,2,'vv',5,'2024-07-25 05:11:15'),(5,5,2,2,'寿司卷太好吃了',5,'2024-07-30 01:23:54');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `searchrecords`
--

DROP TABLE IF EXISTS `searchrecords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `searchrecords` (
  `search_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `search_time` datetime NOT NULL,
  PRIMARY KEY (`search_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `searchrecords_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `searchrecords`
--

LOCK TABLES `searchrecords` WRITE;
/*!40000 ALTER TABLE `searchrecords` DISABLE KEYS */;
INSERT INTO `searchrecords` VALUES (23,3,'关闭你','2024-07-26 08:43:09'),(24,3,'关闭你','2024-07-26 13:34:09'),(25,3,'关闭','2024-07-26 13:34:09'),(26,5,'f','2024-07-30 09:00:43'),(27,5,'寿司卷','2024-07-30 10:06:52'),(28,5,'寿司卷','2024-07-30 10:08:21');
/*!40000 ALTER TABLE `searchrecords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `contact_info` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'张三','123','东校区','广州师范大学大学 电话：15200221'),(2,'李四','lisi123','西校区','广州华南理工大学 电话：15200221'),(3,'1','1','西校区','广东工业大学 电话：15200221'),(4,'a','a','西校区','中山大学 电话：15200221'),(5,'qq','qq','西校区','广州华南理工大学 电话：15200221'),(6,'ff','ff','西校区','中山大学 电话：15200221'),(7,'aa','aa',NULL,'武汉大学'),(8,'qq','qq',NULL,'qq'),(9,'1','1',NULL,'1'),(10,'44','44',NULL,'44'),(11,'22','22',NULL,'22'),(12,'ee','ee',NULL,'ee');
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` varchar(200) NOT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `students` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1,1,'张三','13800138000','南校区1号宿舍楼303室',0,'2025-01-06 10:06:19','2025-01-06 10:15:43'),(2,1,'张三','13800138000','南校区图书馆2楼',1,'2025-01-06 10:06:19','2025-01-07 07:34:51'),(3,2,'李四','13900139000','北校区2号宿舍楼505室',1,'2025-01-06 10:06:19','2025-01-06 10:06:19'),(4,2,'李四','13900139000','北校区教学楼B座',0,'2025-01-06 10:06:19','2025-01-06 10:06:19'),(5,3,'王五','13700137000','南校区3号宿舍楼404室',1,'2025-01-06 10:06:19','2025-01-06 10:06:19'),(6,3,'王五','13700137000','南校区食堂二楼',0,'2025-01-06 10:06:19','2025-01-06 10:06:19'),(7,4,'赵六','13600136000','北校区4号宿舍楼202室',1,'2025-01-06 10:06:19','2025-01-06 10:06:19'),(8,4,'赵六','13600136000','北校区实验楼',0,'2025-01-06 10:06:19','2025-01-06 10:06:19'),(9,5,'孙七','13500135000','南校区5号宿舍楼101室',1,'2025-01-06 10:06:19','2025-01-06 10:06:19'),(10,5,'孙七','13500135000','南校区体育馆',0,'2025-01-06 10:06:19','2025-01-06 10:06:19');
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `userid` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'John','123','profile.jpg');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-15  7:06:34
