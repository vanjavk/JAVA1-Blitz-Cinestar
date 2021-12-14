USE [master];

DECLARE @kill varchar(8000) = '';  
SELECT @kill = @kill + 'kill ' + CONVERT(varchar(5), session_id) + ';'  
FROM sys.dm_exec_sessions
WHERE database_id  = db_id('JAVAPROJ')

EXEC(@kill);
USE master
GO
DROP DATABASE JAVAPROJ
GO
CREATE DATABASE JAVAPROJ COLLATE LATIN1_GENERAL_100_CI_AS_SC_UTF8;
GO
USE JAVAPROJ
GO

CREATE TABLE Users
(
	IDUser INT PRIMARY KEY IDENTITY,
	Username NVARCHAR(32) NOT NULL,
	Password NVARCHAR(256) NOT NULL,
	Admin INT DEFAULT 0,
)
GO

CREATE TABLE Movie
(
	IDMovie INT PRIMARY KEY IDENTITY,
	Title NVARCHAR(512),
	Description NVARCHAR(max),
	Genre NVARCHAR(512),
	Duration INT,
	PublishedDate NVARCHAR(90),
	PicturePath NVARCHAR(1024),
)
GO

CREATE UNIQUE INDEX unique_title ON Movie(Title)
WHERE Title IS NOT NULL

CREATE TABLE Director
(
	IDDirector INT PRIMARY KEY IDENTITY,
	Name NVARCHAR(256) UNIQUE NOT NULL,
)
GO

CREATE TABLE DirectorMovie
(
	IDDirectorMovie INT PRIMARY KEY IDENTITY,
	IDDirector int FOREIGN KEY REFERENCES Director(IDDirector) NOT NULL,
	IDMovie int FOREIGN KEY REFERENCES Movie(IDMovie) NOT NULL,
)
GO

CREATE TABLE Actor
(
	IDActor INT PRIMARY KEY IDENTITY,
	Name NVARCHAR(256) UNIQUE NOT NULL,
)
GO

CREATE TABLE ActorMovie
(
	IDActorMovie INT PRIMARY KEY IDENTITY,
	IDActor int FOREIGN KEY REFERENCES Actor(IDActor) NOT NULL,
	IDMovie int FOREIGN KEY REFERENCES Movie(IDMovie) NOT NULL,
)
GO

INSERT INTO Users VALUES ('Admin','$argon2id$v=19$m=4096,t=3,p=1$n4mNXF3jaH6+jH+AdnNDAg$hckM5kig3zsWsU123KOds+MwqQ5h8T8H2p4zHLdDIls',1) -- password
GO

CREATE PROCEDURE createUser
	@Username NVARCHAR(max),
	@Password NVARCHAR(max),
	@ID INT OUTPUT
AS 
BEGIN 
	IF EXISTS(SELECT * FROM Users U where U.Username=@Username)
	BEGIN
		THROW 50100 , 'User already exists!', 1;
	END
	ELSE
	BEGIN
		INSERT INTO Users VALUES(@Username, @Password,0)
		SET @ID = SCOPE_IDENTITY()
	END
END
GO

CREATE PROCEDURE selectUser
	@Username NVARCHAR(max)
AS 
BEGIN 
	SELECT TOP 1 * FROM Users U where U.Username=@Username
END
GO

CREATE PROCEDURE createMovie
	@Title NVARCHAR(max),
	@Description NVARCHAR(max),
	@Genre NVARCHAR(max),
	@Duration INT,
	@PublishedDate NVARCHAR(max),
	@PicturePath NVARCHAR(max),
	@ID INT OUTPUT
AS 
BEGIN 
	INSERT INTO Movie VALUES(@Title,@Description,@Genre,@Duration,@PublishedDate,@PicturePath)
	SET @ID = SCOPE_IDENTITY()
END
GO


CREATE PROCEDURE selectMovies
AS 
BEGIN 
	SELECT * FROM Movie
END
GO

CREATE PROCEDURE createDirector
	@Name NVARCHAR(max),
	@ID INT OUTPUT
AS 
BEGIN 
	IF EXISTS(SELECT * FROM Director D where D.Name=@Name)
	BEGIN
		SET @ID = (SELECT TOP 1 IDDirector FROM Director D where D.Name=@Name)
	END
	ELSE
	BEGIN
		INSERT INTO Director VALUES(@Name)
		SET @ID = SCOPE_IDENTITY()
	END
END
GO

CREATE PROCEDURE createActor
	@Name NVARCHAR(max),
	@ID INT OUTPUT
AS 
BEGIN
	IF EXISTS(SELECT * FROM Actor A where A.Name=@Name)
	BEGIN
		SET @ID = (SELECT TOP 1 IDActor FROM Actor A where A.Name=@Name)
	END
	ELSE
	BEGIN
		INSERT INTO Actor VALUES(@Name)
		SET @ID = SCOPE_IDENTITY()
	END
END
GO

CREATE PROCEDURE addDirector
	@IDDirector INT,
	@IDMovie INT,
	@ID INT OUTPUT
AS 
BEGIN 
	IF EXISTS(SELECT * FROM DirectorMovie AM where AM.IDDirector=@IDDirector and AM.IDMovie=@IDMovie)
	BEGIN
		SET @ID = (SELECT TOP 1 IDDirectorMovie FROM DirectorMovie AM where AM.IDDirector=@IDDirector and AM.IDMovie=@IDMovie)
	END
	ELSE
	BEGIN
		INSERT INTO DirectorMovie VALUES(@IDDirector,@IDMovie)
		SET @ID = SCOPE_IDENTITY()
	END
END
GO

CREATE PROCEDURE addActor
	@IDActor INT,
	@IDMovie INT,
	@ID INT OUTPUT
AS 
BEGIN 
	IF EXISTS(SELECT * FROM ActorMovie AM where AM.IDActor=@IDActor and AM.IDMovie=@IDMovie)
	BEGIN
		SET @ID = (SELECT TOP 1 IDActorMovie FROM ActorMovie AM where AM.IDActor=@IDActor and AM.IDMovie=@IDMovie)
	END
	ELSE
	BEGIN
		INSERT INTO ActorMovie VALUES(@IDActor,@IDMovie)
		SET @ID = SCOPE_IDENTITY()
	END
END
GO

CREATE PROCEDURE selectDirectors
	@IDMovie INT = NULL
AS 
BEGIN
	IF (@IDMovie IS NULL)
	BEGIN
		SELECT * FROM Director 
	END
	ELSE
	BEGIN
		SELECT * FROM Director D WHERE D.IDDirector IN (SELECT IDDirector FROM DirectorMovie DM WHERE DM.IDMovie=@IDMovie)
	END
END
GO

CREATE PROCEDURE selectActors
	@IDMovie INT = NULL
AS 
BEGIN 
	IF (@IDMovie IS NULL)
	BEGIN
		SELECT * FROM Actor 
	END
	ELSE
	BEGIN
		SELECT * FROM Actor A WHERE A.IDActor IN (SELECT IDActor FROM ActorMovie AM WHERE AM.IDMovie=@IDMovie)
	END
END
GO

CREATE PROCEDURE selectMovie
	@IDMovie INT
AS 
BEGIN 
	SELECT TOP 1 * FROM Movie M WHERE M.IDMovie=@IDMovie
END
GO

CREATE PROCEDURE selectDirector
	@IDDirector INT
AS 
BEGIN 
	SELECT TOP 1 * FROM Director D WHERE D.IDDirector=@IDDirector
END
GO

CREATE PROCEDURE selectActor
	@IDActor INT
AS 
BEGIN 
	SELECT TOP 1 * FROM Actor A WHERE A.IDActor=@IDActor
END
GO

CREATE PROCEDURE updateMovie
	@IDMovie INT,
	@Title NVARCHAR(max),
	@Description NVARCHAR(max),
	@Genre NVARCHAR(max),
	@Duration INT,
	@PublishedDate NVARCHAR(max),
	@PicturePath NVARCHAR(max)
AS 
BEGIN 
	UPDATE MOVIE SET Title=@Title, Description=@Description, Genre=@Genre, Duration=@Duration, PublishedDate=@PublishedDate, PicturePath=@PicturePath WHERE IDMovie=@IDMovie
	SELECT TOP 1 * FROM Movie M WHERE M.IDMovie=@IDMovie
END
GO

CREATE PROCEDURE updateDirector
	@IDDirector INT,
	@Name NVARCHAR(MAX)
AS 
BEGIN
	UPDATE Director SET Name=@Name WHERE IDDirector=@IDDirector
	SELECT TOP 1 * FROM Director D WHERE D.IDDirector=@IDDirector
END
GO

CREATE PROCEDURE updateActor
	@IDActor INT,
	@Name NVARCHAR(MAX)
AS 
BEGIN
	UPDATE Actor SET Name=@Name WHERE IDActor=@IDActor
	SELECT TOP 1 * FROM Actor A WHERE A.IDActor=@IDActor
END
GO

CREATE PROCEDURE deleteMovie
	@IDMovie INT
AS 
BEGIN
	DELETE DirectorMovie WHERE IDMovie=@IDMovie
	DELETE ActorMovie WHERE IDMovie=@IDMovie
	DELETE Movie WHERE IDMovie=@IDMovie
END
GO

CREATE PROCEDURE deleteDirector
	@IDDirector INT
AS 
BEGIN
	DELETE Director WHERE IDDirector=@IDDirector
END
GO

CREATE PROCEDURE deleteActor
	@IDActor INT
AS 
BEGIN
	DELETE Actor WHERE IDActor=@IDActor
END
GO

CREATE PROCEDURE removeDirector
	@IDDirector INT,
	@IDMovie INT
AS 
BEGIN
	DELETE DirectorMovie WHERE IDDirector=@IDDirector AND IDMovie=@IDMovie
END
GO

CREATE PROCEDURE removeActor
	@IDActor INT,
	@IDMovie INT
AS 
BEGIN
	DELETE ActorMovie WHERE IDActor=@IDActor AND IDMovie=@IDMovie
END
GO

CREATE PROCEDURE clearDatabase
AS
BEGIN
	DELETE ActorMovie
	DELETE DirectorMovie
	DELETE Movie
	DELETE Actor
	DELETE Director
END
GO

USE master 
GO