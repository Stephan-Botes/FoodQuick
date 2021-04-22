/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [id]
      ,[order_number]
      ,[item_number]
      ,[qty]
  FROM [QuickFoodMS].[dbo].[OrderItems]

ALTER TABLE [QuickFoodMS].[dbo].[OrderItems]
	ADD id INT NOT NULL IDENTITY(1,1);

INSERT INTO [QuickFoodMS].[dbo].[OrderItems] (order_number, item_number, qty)
	VALUES (12345678, 7, 2),
	(12345678, 13, 2),
	(12345678, 10, 1),
	(9764281, 3, 5),
	(24562415, 16, 2),
	(5546321, 21, 1),
	(5546321, 27, 1),
	(5546321, 24, 2),
	(1111111, 28, 1),
	(987654321, 34, 1),
	(29879871, 37, 1),
	(29879871, 40, 1),
	(29879871, 38, 1),
	(4206988, 9, 2),
	(4206988, 12, 1),
	(4206988, 10, 2)