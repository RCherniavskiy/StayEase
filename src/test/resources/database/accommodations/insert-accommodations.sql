INSERT INTO addresses (id, country, state, city, district, street, house_number, apartment_number, floor, zip_code)
VALUES (1, 'Ukraine', 'Kyiv Region', 'Kyiv', NULL, 'Khreshchatyk St.', '1', NULL, NULL, '01001');
INSERT INTO accommodations (id, type_id, address_id, size_type_id, daily_rate)
VALUES (1, 1, 1, 1, 120);
INSERT INTO accommodations_amenity_types (accommodation_id, amenity_type_id)
VALUES (1, 1);
INSERT INTO accommodations_amenity_types (accommodation_id, amenity_type_id)
VALUES (1, 2);

INSERT INTO addresses (id, country, state, city, district, street, house_number, apartment_number, floor, zip_code)
VALUES (2, 'Ukraine', 'Kyiv Region', 'Kyiv', NULL, 'Khreshchatyk St.', '1', NULL, NULL, '01001');
INSERT INTO accommodations (id, type_id, address_id, size_type_id, daily_rate)
VALUES (2, 1, 1, 1, 120);
INSERT INTO accommodations_amenity_types (accommodation_id, amenity_type_id)
VALUES (2, 2);
INSERT INTO accommodations_amenity_types (accommodation_id, amenity_type_id)
VALUES (2, 1);

INSERT INTO addresses (id, country, state, city, district, street, house_number, apartment_number, floor, zip_code)
VALUES (3, 'Ukraine', 'Kyiv Region', 'Kyiv', NULL, 'Khreshchatyk St.', '1', NULL, NULL, '01001');
INSERT INTO accommodations (id, type_id, address_id, size_type_id, daily_rate)
VALUES (3, 1, 1, 1, 120);
INSERT INTO accommodations_amenity_types (accommodation_id, amenity_type_id)
VALUES (3, 3);
INSERT INTO accommodations_amenity_types (accommodation_id, amenity_type_id)
VALUES (3, 4);



