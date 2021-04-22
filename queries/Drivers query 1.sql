/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [id]
      ,[firstname]
      ,[surname]
      ,[city]
      ,[load]
  FROM [QuickFoodMS].[dbo].[Drivers]

ALTER TABLE [QuickFoodMS].[dbo].[Drivers]
	ADD id INT NOT NULL IDENTITY(1,1);

INSERT INTO [QuickFoodMS].[dbo].[Drivers] (id, firstname, surname, city, [load])
	VALUES ('Julie', 'Carty', 'Cape Town', 6),
	('Karol', 'Dunn', 'Durban', 4),
	('Spike', 'Fenton', 'Johannesburg', 6),
	('Eugene', 'Santana', 'Durban', 2),
	('Cayson', 'Warner', 'Cape Town', 3),
	('Eisa', 'Wilson', 'Johannesburg', 11),
	('Gemma', 'Paterson', 'Johannesburg', 12),
	('Tyron', 'Bonilla', 'Johannesburg', 14),
	('Victor', 'Orozco', 'Potchefstroom', 8),
	('Aya', 'Farrington', 'Cape Town', 9),
	('Johan', 'Hoffman', 'Springbok', 2),
	('Kaelan', 'Casey', 'Bloemfontein', 16),	
	('Kealan', 'Chester', 'Port Elizabeth', 3),
	('Kailan', 'Snow', 'Bloemfontein', 6),
	('Ana', 'Ortega', 'Port Elizabeth', 2),
	('Jaidan', 'Spencer', 'Potchefstroom', 3),
	('Kallum', 'Sadler', 'Witbank', 15),
	('Aaron', 'Neville', 'Cape Town', 4),
	('Trevor', 'Rigby', 'Bloemfontein', 27),
	('Eshan', 'Gibson', 'Witbank', 4),
	('Abul', 'Ali', 'Durban', 12),
	('Liya', 'Simons', 'Springbok', 5),
	('Umayr', 'Rawlings', 'Durban', 6),						
	('Adelina', 'Markham', 'Witbank', 7),
	('Huma', 'Owens', 'Witbank', 12),
	('Miranda', 'Metcalfe', 'Cape Town', 2),
	('Caitlin', 'Andrade', 'Witbank', 8),
	('Blaine', 'Merritt', 'Springbok', 11),
	('Clement', 'Bond', 'Port Elizabeth', 9),
	('Lily', 'Drew', 'Port Elizabeth', 4)		