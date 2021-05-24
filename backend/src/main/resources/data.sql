INSERT INTO address (street_number, street_name, city, region, country, postcode) VALUES
    /* 25 addresses */
    ('44', 'Menomonie Way', 'Zhashkiv', null, 'Ukraine', null),
    ('8120', 'GroverJunction', 'Cimarelang', null, 'Indonesia', null),
    ('5305', 'Melrose Drive', 'Sidon', null, 'Lebanon', null),
    ('39', 'Longview Hill', 'Drochia', null, 'Moldova', 'MD-5233'),
    ('5', 'Hagan Avenue', 'Chengdong', null, 'China', null),
    ('5776', 'Nancy Point', 'Bijeli', null, 'Indonesia', null),
    ('01801', 'Grasskamp Lane', 'Malhou', 'Santarém', 'Portugal', '2380-506'),
    ('5', 'Utah Terrace', 'Huangfang', null, 'China', null),
    ('9885', 'Oak Parkway', 'Ambato', null, 'Ecuador', null),
    ('976', 'Heffernan Pass', 'Kan’onjichō', null, 'Japan', '769-1613'),
    ('26', 'Prentice Lane', 'Delvinë', null, 'Albania', null),
    ('11', 'Lien Street', 'Guishan', null, 'China', null),
    ('78649', 'Colorado Pass', 'Zhonglong', null, 'China', null),
    ('39353', 'Oak Valley Road', 'Kinmparana', null, 'Mali', null),
    ('0', 'Bashford Road', 'Piracicaba', null, 'Brazil', '13400-000'),
    ('910', 'Granby Street', 'Cimara', null, 'Indonesia', null),
    ('7', 'Starling Place', 'Congkar', null, 'Indonesia', null),
    ('6324', 'Superior Lane', 'Vänersborg', 'Västra Götaland', 'Sweden', '462 22'),
    ('6', 'Cambridge Crossing', 'Koungou', null, 'Mayotte', '97600'),
    ('0056', 'Dennis Hill', 'Néa Flogitá', null, 'Greece', null),
    /* Business Address - 21 -> 25 */
    ('0', 'Vernon Place', 'Sarpang', null, 'Bhutan', null),
    ('95403', 'Hanover Park', 'El Guapinol', null, 'Honduras', null),
    ('6794', 'Jackson Way', 'Xialaba', null, 'China', null),
    ('2557', 'Starling Pass', 'Skinnskatteberg', 'Västmanland', 'Sweden', '739 30'),
    ('302', 'Forest Run Place', 'Putinci', null, 'Serbia', null);


INSERT INTO user (first_name, middle_name, last_name, nickname, email, bio, date_of_birth, phone_number, password, created, role, address_id) VALUES
    /* 20 users */
    ('Wilma', 'Janet', 'Sails', 'Open-architected', 'jsails0@go.com', 'Profit-focused scalable moratorium', '1989-02-28', '+57 242 190 0153', 't146MwLm', '2020-08-07 11:35:52', 'USER', 1),
    ('Karrie', 'Salvatore', 'Loyley', 'local area network', 'sloyley1@wordpress.com', 'Quality-focused next generation synergy', '1995-11-06', '+48 864 927 4819', 'VhLBH0', '2020-05-01 13:25:12', 'USER', 2),
    ('Felice', 'Tabbitha', 'Jaeggi', 'intranet', 'tjaeggi2@independent.co.uk', 'Managed foreground budgetary management', '1976-12-06', '+1 659 270 1003', '7o3x3SS', '2020-12-25 06:40:59', 'USER', 3),
    ('Holt', 'Vincent', 'Skelhorne', 'impactful', 'vskelhorne3@washington.edu', 'Extended intermediate budgetary management', '2005-05-08', '+86 537 663 8760', 'hT4e8e65', '2020-06-22 18:17:39', 'USER', 4),
    ('Shermy', 'Pearle', 'Layborn', 'artificial intelligence', 'playborn4@amazon.com', 'Intuitive client-server standardization', '2000-12-30', '+62 527 277 7359', 'VMSR0U', '2020-10-17 13:47:11', 'USER', 5),
    /* Above are primary admins, below are regular admins. */
    ('Keelby', 'Hersch', 'Ganiclef', 'leading edge', 'hganiclef5@reference.com', 'Future-proofed client-server complexity', '1983-07-17', '+7 599 583 7488', 'KPseG96VKKqd', '2021-03-20 15:05:43', 'USER', 6),
    ('Papageno', 'Batholomew', 'Dolton', 'Persevering', 'bdolton6@liveinternet.ru', 'Advanced bi-directional flexibility', '2000-07-12', '+380 428 944 6622', 'SmEBX59m9Y0', '2020-07-21 15:54:32', 'USER', 7),
    ('Elysee', 'Maurene', 'Took', 'benchmark', 'mtook7@chron.com', 'Team-oriented interactive installation', '1985-11-09', '+234 186 824 2303', 'qADcH893ZcG', '2020-11-01 11:39:42', 'USER', 8),
    ('Ruthe', 'Ogdan', 'Ruit', 'Open-architected', 'oruit8@reddit.com', 'Configurable coherent capacity', '1995-05-10', '+62 283 517 0351', 'KPLrxm2', '2020-09-25 06:01:34', 'USER', 9),
    ('Ruthe', 'Iosep', 'Yoseloff', 'systematic', 'iyoseloff9@elegantthemes.com', 'Multi-tiered static toolset', '2003-05-11', '+420 882 542 6613', 'YOETusNwIdb', '2020-07-17 12:25:47', 'USER', 10),
    /* Rest is normal users. */
    ('Gris', 'Carlotta', 'Woodwin', 'zero administration', 'cwoodwina@samsung.com', 'Multi-tiered secondary pricing structure', '2005-04-15', '+1 260 493 4589', '5eVUsp6s3g', '2021-03-10 11:55:53', 'USER', 11),
    ('Guinevere', 'Kath', 'Grahamslaw', 'Phased', 'kgrahamslawb@wisc.edu', 'Optimized 6th generation capacity', '1990-08-18', '+235 527 784 2956', 'ME0feNfd', '2020-06-26 05:08:51', 'USER', 12),
    ('Ros', 'Maribel', 'Drable', 'Front-line', 'mdrablec@hc360.com', 'Reduced mobile ability', '1980-09-09', '+506 894 444 0931', 'VdP0lufryC', '2021-01-03 15:22:29', 'USER', 13),
    ('Mellicent', null, 'Foucar', null, 'gfoucard@huffingtonpost.com', null, '1996-08-09', null, 'VdPaJk0luFryC', '2020-07-27 19:54:26', 'USER', 14),
    ('Chiarra', 'Dyanne', 'Phelip', 'context-sensitive', 'dphelipe@godaddy.com', 'Synergized scalable attitude', '2002-01-04', '+93 374 108 2586', 'fZ2KmBsEr0', '2020-04-26 06:23:01', 'USER', 15),
    ('Mark', null, 'Snibson', null, 'bsnibsonf@eepurl.com', null, '1975-10-26', null, 'VdP0aufrqyC', '2021-03-10 23:48:06', 'USER', 16),
    ('Lexis', 'Dorri', 'Garbert', 'Enhanced', 'dgarbertg@npr.org', 'Balanced multi-state intranet', '1993-08-18', '+63 192 437 4906', 'aC46V7uAs1ur', '2020-07-11 09:33:36', 'USER', 17),
    ('Giselbert', 'Gates', 'Sleney', 'client-server', 'gsleneyh@cbslocal.com', 'Diverse responsive info-mediaries', '1994-11-16', '+7 106 973 4702', 'snqO88', '2020-05-17 16:58:23', 'USER', 19),
    ('Adan', 'Rickey', 'McGilvra', 'Inverse', 'rmcgilvrai@opera.com', 'Public-key static archive', '2003-01-21', '+48 366 279 7985', 'qjMGWWf', '2020-12-17 02:16:35', 'USER', 19),
    ('Pamella', 'Maighdiln', 'Poupard', 'Persevering', 'mpoupardj@bluehost.com', 'Grass-roots asymmetric success', '1992-09-17', '+963 350 483 0624', 'Uq9LpBxyeQ', '2021-04-02 20:24:58', 'USER', 20);

/* MARIADB FUNCTION: SHA2(password, 256); */
UPDATE user SET password = HASH('SHA256', password);

INSERT INTO business (name, description, created, user_id, business_type, address_id) VALUES
    /* 5 businesses */
    ('Dabshots', 'Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.', '2020-05-18 21:06:11', 1, 'CHARITABLE_ORGANISATION', 21),
    ('Layo', 'Nam ultrices, libero non mattis pulvinar, nulla pede ullamcorper augue, a suscipit nulla elit ac nulla. Sed vel enim sit amet nunc viverra dapibus. Nulla suscipit ligula in lacus.', '2020-08-25 22:22:19', 2, 'ACCOMMODATION_AND_FOOD_SERVICES', 22),
    ('Skinder', 'Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.', '2020-09-11 20:50:50', 3, 'RETAIL_TRADE', 23),
    ('Skiptube', 'Phasellus in felis.', '2020-10-18 10:30:14', 4, 'RETAIL_TRADE', 24),
    ('Photojam', 'Praesent blandit. Nam nulla.', '2020-05-23 08:21:22', 5, 'RETAIL_TRADE', 25);


INSERT INTO business_admins (business_id, user_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (3, 7),
    (5, 9),
    (4, 6),
    (5, 8),
    (2, 9),
    (2, 1),
    (3, 1);



INSERT INTO product (id, name, description, manufacturer, recommended_retail_price, created, business_id) VALUES
    /* 20 products */
    ('WAUEH98E37A680027', 'Wine - White, Pinot Grigio', 'integer ac leo pellentesque ultrices mattis odio donec vitae nisi nam ultrices libero non mattis pulvinar', 'Bubbletube', 51.63, '2021-01-27 16:07:14', 2),
    ('WAUVT64B54N722288', 'Pastry - Cheese Baked Scones', 'amet erat nulla tempus vivamus', 'Watties', 19.88, '2021-03-05 14:36:54', 1),
    ('KNDJT2A29D7324794', 'Beef - Ground Lean Fresh', 'vestibulum ante ipsum primis in faucibus', 'Watties', 59.43, '2021-02-06 20:17:16', 3),
    ('W04GP5EC0B1798680', 'Compound - Mocha', 'vel ipsum praesent blandit lacinia erat vestibulum sed magna at nunc', 'Nestle', 88.93, '2021-01-11 20:54:46', 1),
    ('SCFEBBCF6CG218162', 'Yogurt - Blueberry, 175 Gr', 'nec nisi volutpat eleifend donec ut dolor morbi vel lectus in quam fringilla rhoncus mauris enim leo', 'Gabcube', 72.83, '2021-01-15 16:56:26', 5),
    ('WBAPM7C51AE969438', 'Longos - Lasagna Veg', 'a pede posuere nonummy integer non velit donec diam neque vestibulum eget vulputate ut ultrices vel augue vestibulum ante ipsum', 'Skimia', 36.81, '2021-03-04 03:38:04', 2),
    ('5UXWX7C53EL958298', 'Ice Cream Bar - Drumstick', 'eu mi nulla ac enim', 'Formula Foods', 50.2, '2021-01-01 07:57:56', 5),
    ('WBAVD33586K975326', 'Peas Snow', 'porttitor pede justo eu massa donec dapibus duis at velit eu est congue elementum', 'Peapea Foods', 73.9, '2021-04-06 19:28:28', 2),
    ('5NPDH4AE0EH887279', 'Cheese - Pied De Vents', 'eget congue eget semper rutrum nulla nunc purus phasellus in felis donec semper sapien a libero nam dui', 'Food Food Food', 36.65, '2021-01-24 21:22:47', 5),
    ('2C3CDZFJ2FH936197', 'Sherbet - Raspberry', 'justo sollicitudin ut suscipit a feugiat et eros vestibulum ac est', 'Sherbet Co', 86.18, '2021-01-28 19:33:16', 2),
    ('WBAUP9C53DV435366', 'Energy Drink', 'ipsum dolor sit amet consectetuer adipiscing elit proin', 'Coca-Cola', 91.48, '2021-03-04 02:58:24', 4),
    ('YV1612FS9E2422348', 'Bread Base - Gold Formel', 'condimentum neque sapien placerat ante nulla justo', 'Coopolandes', 29.81, '2021-01-08 21:31:51', 3),
    ('WAUMF98KX9A331487', 'Chicken - White Meat With Tender', 'in eleifend quam a odio in hac habitasse platea dictumst maecenas ut massa quis augue luctus', 'Chicken Little Ltd', 30.91, '2021-01-03 04:39:36', 3),
    ('5UXFG8C5XAL953759', 'Yogurt - Strawberry, 175 Gr', 'justo lacinia eget tincidunt eget tempus vel pede morbi porttitor lorem id ligula suspendisse ornare consequat', 'Formula Foods', 95.84, '2021-02-10 6:35:49', 3),
    ('WAUJC58E75A227743', 'Fish - Scallops, Cold Smoked', 'rutrum at lorem integer tincidunt ante vel ipsum praesent blandit lacinia erat vestibulum sed', 'United Fisheries', 65.12, '2021-02-03 19:00:32', 2),
    ('1N6AA0CA2AN447971', 'Wine - Bouchard La Vignee Pinot', 'bibendum morbi non quam nec dui luctus rutrum nulla tellus in sagittis dui', 'Lorem Ipsum', 41.35, '2021-03-16 16:26:06', 5),
    ('SCBDR3ZA2BC827880', 'Nestea - Iced Tea', 'a suscipit nulla elit ac nulla sed vel enim sit amet nunc viverra dapibus nulla suscipit ligula in lacus curabitur', 'Pepsico', 24.27, '2021-01-27 18:14:57', 5),
    ('WAUUL98E96A125348', 'Calvados - Boulard', 'quis lectus suspendisse potenti in eleifend quam a', 'Vegesdirect', 9.03, '2021-01-25 22:18:45', 3),
    ('JN8AF5MR1ET433412', 'Juice - Pineapple, 341 Ml', 'ridiculus mus etiam vel augue vestibulum', 'Juicelord', 2.66, '2021-03-09 22:56:37', 3),
    ('WAUEH78E77A454447', 'Bread - Focaccia Quarter', 'orci vehicula condimentum curabitur in libero', 'Bread Company', 22.34, '2021-04-05 18:35:12', 5);

INSERT INTO inventory (best_before, business_id, expires, manufactured, price_per_item, product_id, quantity, sell_by, total_price) VALUES
    ('2021-05-27T12:00:00Z', 1, '2021-05-27T12:00:00Z', '2021-01-27T12:00:00Z', 5.00, 'WAUVT64B54N722288', 10, '2021-05-25T12:00:00Z', 50.00),
    -- ('2021-05-27T12:00:00Z', 1, '2021-05-27T12:00:00Z', null, 2.00, '07-4957066', 5, null, 30.00);
    ('2021-08-27T12:00:00Z', 1, '2021-08-27T12:00:00Z', '2020-01-27T12:00:00Z', 3.00, 'W04GP5EC0B1798680', 7, null, 80.00),
    ('2021-08-27T12:00:00Z', 1, '2021-10-27T12:00:00Z', '2021-01-27T12:00:00Z', 1.00, 'WAUVT64B54N722288', 9, null, 50.00),
    ('2021-09-27T12:00:00Z', 1, '2021-11-27T12:00:00Z', '2021-01-27T12:00:00Z', 6.00, 'WAUVT64B54N722288', 1, null, 50.00),
    ('2021-09-27T12:00:00Z', 1, '2021-11-27T12:00:00Z', '2021-01-27T12:00:00Z', 6.00, 'WAUVT64B54N722288', 1, null, 50.00),
    ('2021-09-27T12:00:00Z', 1, '2021-11-27T12:00:00Z', '2021-01-27T12:00:00Z', 6.00, 'WAUVT64B54N722288', 1, null, 50.00);

INSERT INTO listing (closes, created, more_info, price, quantity, inventory_item_id) VALUES
    ('2021-09-22 12:00:00 ', '2021-01-27 12:00:00 ', 'Not negotiable.', 10, 3, 1),
    ('2021-10-27 12:00:00 ', '2021-01-28 12:00:00 ', 'Could be negotiable.', 10, 5, 1),
    ('2021-11-12 12:00:00 ', '2021-02-01 12:00:00 ', 'Price is negotiable.', 10, 5, 1),
    ('2021-09-08 12:00:00 ', '2021-02-02 12:00:00 ', 'Contact us for more information.', 15.5, 1, 2),
    ('2021-10-19 12:00:00', '2021-02-03 12:00:00 ', 'Do not contact us for more information.', 5.99, 2, 2);




