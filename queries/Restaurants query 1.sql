/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [id]
      ,[number]
      ,[name]
      ,[city]
  FROM [QuickFoodMS].[dbo].[Restaurants]

ALTER TABLE [QuickFoodMS].[dbo].[Restaurants]
	ADD id INT NOT NULL IDENTITY(1,1);

INSERT INTO [QuickFoodMS].[dbo].[Restaurants] (number, [name], city)
	VALUES ('0316725498', 'Roman''s Pizza', 'Durban'),
	('0182933710', 'Roman''s Pizza', 'Potchefstroom'),
	('0277121890', 'Roman''s Pizza', 'Springbok'),
	('0135906565', 'Roman''s Pizza', 'Witbank'),
	('0215518680', 'Roman''s Pizza', 'Cape Town'),
	('0118339321', 'Roman''s Pizza', 'Johannesburg'),
	('0514300132', 'Roman''s Pizza', 'Bloemfontein'),
	('0413687296', 'Roman''s Pizza', 'Port Elizabeth'),
	('0313216548', 'Steers', 'Durban'),
	('0189431657', 'Steers', 'Potchefstroom'),
	('0272245446', 'Steers', 'Springbok'),
	('0134685732', 'Steers', 'Witbank'),
	('0218493145', 'Steers', 'Cape Town'),
	('0119461375', 'Steers', 'Johannesburg'),
	('0519975346', 'Steers', 'Bloemfontein'),
	('0414567893', 'Steers', 'Port Elizabeth'),
	('0311597534', 'KFC', 'Durban'),
	('0188523694', 'KFC', 'Potchefstroom'),
	('0279765184', 'KFC', 'Springbok'),
	('0133352624', 'KFC', 'Witbank'),
	('0210375934', 'KFC', 'Cape Town'),
	('0115287419', 'KFC', 'Johannesburg'),
	('0519764385', 'KFC', 'Bloemfontein'),
	('0413216547', 'KFC', 'Port Elizabeth'),
	('0319514568', 'Nando''s', 'Durban'),
	('0181478963', 'Nando''s', 'Potchefstroom'),
	('0273573684', 'Nando''s', 'Springbok'),
	('0132425273', 'Nando''s', 'Witbank'),
	('0211253856', 'Nando''s', 'Cape Town'),
	('0111236548', 'Nando''s', 'Johannesburg'),
	('0511477852', 'Nando''s', 'Bloemfontein'),
	('0411598524', 'Nando''s', 'Port Elizabeth'),
	('0311478253', 'McDonald''s', 'Durban'),
	('0184679132', 'McDonald''s', 'Potchefstroom'),
	('0271591473', 'McDonald''s', 'Springbok'),
	('0131646786', 'McDonald''s', 'Witbank'),
	('0219845713', 'McDonald''s', 'Cape Town'),
	('0119845327', 'McDonald''s', 'Johannesburg'),
	('0512258834', 'McDonald''s', 'Bloemfontein'),
	('0413468752', 'McDonald''s', 'Port Elizabeth'),
	('0316875492', 'Debonairs Pizza', 'Durban'),
	('0189746315', 'Debonairs Pizza', 'Potchefstroom'),
	('0271231647', 'Debonairs Pizza', 'Springbok'),
	('0139865324', 'Debonairs Pizza', 'Witbank'),
	('0212266345', 'Debonairs Pizza', 'Cape Town'),
	('0117436951', 'Debonairs Pizza', 'Johannesburg'),
	('0519788653', 'Debonairs Pizza', 'Bloemfontein'),
	('0414679315', 'Debonairs Pizza', 'Port Elizabeth')
