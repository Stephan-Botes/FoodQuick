/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [id]
      ,[number]
      ,[email]
      ,[firstname]
      ,[surname]
      ,[address]
      ,[city]
  FROM [QuickFoodMS].[dbo].[Customers]

ALTER TABLE [QuickFoodMS].[dbo].[Customers]
	ADD id INT NOT NULL IDENTITY(1,1);

DELETE FROM [QuickFoodMS].[dbo].[Customers]

INSERT INTO [QuickFoodMS].[dbo].[Customers] (number, email, firstname, surname, [address], city)
	VALUES ('0833979806', 'steph@gg.ez', 'Stephan', 'Botes', '43 Superstreet - Lyttelton Manor', 'Johannesburg'),
	('0715462843', 'jannesva@yahoo.com', 'Jannes', 'van Aswegen', '69 Nupenter street - Drakenfell', 'Cape Town'),
	('0827091929', 'cmounton@gmail.com', 'Coen', 'Mouton', '2 Devender lane - Aerorand', 'Potchefstroom'),
	('0681456232', 'jacques.kloeg@artiflex.co.za', 'Jacques', 'Kloeg', '123 Somewhere street - Moot', 'Durban'),
	('0819665445', 'dbot1@hypermail.org', 'Dylan', 'Botha', '54 Richboy avenue - Hollybark', 'Bloemfontein'),
	('0753986687', 'cyril@pascoe.net', 'Cyril', 'Pascoe', '53 Deserted lane - Sandstorm', 'Springbok'),
	('0897655987', 'pietiejabz@heehee.co.za', 'Pieter', 'Jabzter', '69 Where am I street - Revonia', 'Cape Town')