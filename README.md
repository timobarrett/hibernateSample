# HibernateWork
Simplified application maintaing a mySQL database using hibernate.  Connection pool manager not implemented.  
Data is processed from queried JSON returned values.  Data is added or updated.  mySQL tables are limited 
to a location table and a weather table. The weather table contains a foreign key nwhich consists of the ID
for the location.  The weather table has a unique constraint on the date column ensuring one record is stored 
per day.  
