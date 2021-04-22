/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [id]
      ,[name]
      ,[price]
      ,[restaurant]
  FROM [QuickFoodMS].[dbo].[Items]

ALTER TABLE [QuickFoodMS].[dbo].[Items]
	ADD id INT NOT NULL IDENTITY(1,1);

INSERT INTO [QuickFoodMS].[dbo].[Items] ([name], price, restaurant)
	VALUES ('Hawaiian Pizza', 69.95, 'Roman''s Pizza'),
	('Sweet Chilli Chicken Pizza', 99.99, 'Roman''s Pizza'),
	('Tangy Russian Pizza', 95.90, 'Roman''s Pizza'),
	('Coke', 21.50, 'Roman''s Pizza'),
	('Sprite', 20.50, 'Roman''s Pizza'),
	('Fanta', 19.50, 'Roman''s Pizza'),
	('King Burger', 89.90, 'Steers'),
	('Cheese Burger', 59.95, 'Steers'),
	('Prince Burger',  79.99, 'Steers'),
	('Fries', 19.95, 'Steers'),
	('Coke',  21.50, 'Steers'),
	('Sprite', 20.50, 'Steers'),
	('Pepsi', 22.50, 'Steers'),
	('Streetwise 2', 29.95, 'KFC'),
	('21 Piece Bucket', 274.90, 'KFC'),
	('8 Piece Family Meal', 199.99, 'KFC'),
	('Fries', 23.50, 'KFC'),
	('Fanta', 21.50,'KFC'),
	('Coke', 22.50, 'KFC'),
	('Creme Soda', 21.50, 'KFC'),
	('1/4 Chicken Meal', 79.90, 'Nando''s'),
	('Boujee Bowl', 61.95, 'Nando''s'),
	('Chicken Strips', 54.99, 'Nando''s'),
	('Coleslaw', 29.90, 'Nando''s'),
	('Soft Drink', 19.50, 'Nando''s'),
	('Juice', 16.50, 'Nando''s'),
	(' Smoothie', 17.50, 'Nando''s'),
	('McChicken Meal', 57.90, 'McDonald''s'),
	('Colonel Burger', 75.99, 'McDonald''s'),
	('Chicken Wrap', 44.99, 'McDonald''s'),
	('Fries', 21.95, 'McDonald''s'),
	('Mountain Dew', 23.50, 'McDonald''s'),
	('Milkshake', 18.50, 'McDonald''s'),
	('Slurpie', 20.50, 'McDonald''s'),
	('3 Cheeses Pizza', 84.99, 'Debonairs Pizza'),
	('Meaty Feast Pizza', 92.99, 'Debonairs Pizza'),
	('Bacon and Feta Pizza', 89.90, 'Debonairs Pizza'),
	('Pizza Pie', 33.95, 'Debonairs Pizza'),
	('Coke', 22.50, 'Debonairs Pizza'),
	('Ice Tea', 20.50, 'Debonairs Pizza'),
	('Juice', 16.50, 'Debonairs Pizza')