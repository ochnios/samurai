USE [master]
GO
CREATE LOGIN [samurai-be] WITH PASSWORD=N'samurai-be', DEFAULT_DATABASE=[samurai-db], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON
GO


USE [samurai-db]
GO
CREATE USER [samurai-be] FOR LOGIN [samurai-be]
GO
EXEC sp_addrolemember N'db_owner', N'samurai-be'
GO
