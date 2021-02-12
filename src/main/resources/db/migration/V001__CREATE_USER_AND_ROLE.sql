CREATE TABLE `users` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) NOT NULL UNIQUE,
  `password` varchar(128) NOT NULL,
  `expected_calories_per_day` int NOT NULL,
  `email` varchar(320) NOT NULL UNIQUE,
  `created_by` VARCHAR(255),
  `created_date` DATETIME,
  `last_modified_by` VARCHAR(255),
  `last_modified_date` DATETIME,
  PRIMARY KEY (`id`)
);

CREATE TABLE `roles` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `created_by` VARCHAR(255),
  `created_date` DATETIME,
  `last_modified_by` VARCHAR(255),
  `last_modified_date` DATETIME,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);

CREATE TABLE `users_roles` (
  `user_id` BIGINT(11) NOT NULL,
  `role_id` BIGINT(11) NOT NULL,
  KEY `user_fk_idx` (`user_id`),
  KEY `role_fk_idx` (`role_id`),
  CONSTRAINT `role_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `calories` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`user_id` BIGINT NOT NULL,
	`datetime` DATETIME NOT NULL,
	`num_calories` int NOT NULL,
	`meal_details` VARCHAR(255),
	`created_by` VARCHAR(255),
	`created_date` DATETIME,
	`last_modified_by` VARCHAR(255),
	`last_modified_date` DATETIME,
	 PRIMARY KEY (`id`),
	 CONSTRAINT `calories_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `calories_per_day` (
`date` DATE NOT NULL,
`user_id` BIGINT NOT NULL,
`total_calories` int NOT NULL,
`created_by` VARCHAR(255),
`created_date` DATETIME,
`last_modified_by` VARCHAR(255),
`last_modified_date` DATETIME,
PRIMARY KEY (user_id, date),
CONSTRAINT `calories_per_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);
