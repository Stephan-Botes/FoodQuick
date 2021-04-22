/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [id]
      ,[firstname]
      ,[surname]
      ,[city]
      ,[load]
  FROM [QuickFoodMS].[dbo].[Drivers]

SELECT TOP 1 * 
	FROM [QuickFoodMS].[dbo].[Drivers]
	WHERE city = 'Johannesburg' ORDER BY load ASC;

WITH cityDriverRank AS (
SELECT ROW_NUMBER() OVER(ORDER BY load ASC) AS Rank, *
FROM [QuickFoodMS].[dbo].[Drivers]
WHERE city = 'Johannesburg')
SELECT * FROM cityDriverRank WHERE Rank = 1;

WITH cityDriverRank AS (
SELECT ROW_NUMBER() OVER(PARTITION BY city ORDER BY load ASC) AS Rank, *
FROM [QuickFoodMS].[dbo].[Drivers])
SELECT * FROM cityDriverRank WHERE Rank = 1;