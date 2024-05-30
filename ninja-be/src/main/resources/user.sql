USE [master]
GO
CREATE LOGIN [ninja-be] WITH PASSWORD=N'ninja-be', DEFAULT_DATABASE=[ninja-db], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON
GO

USE [ninja-db]
GO
CREATE USER [ninja-be] FOR LOGIN [ninja-be]
GO
EXEC sp_addrolemember N'db_owner', N'ninja-be'
GO
