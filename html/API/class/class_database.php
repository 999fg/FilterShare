<?php
require_once('class_hidden.php');
/**
 * PDO SINGLETON CLASS
 *  
 * @author Tony Landis
 * @link http://www.tonylandis.com
 * @license Use how you like it, just please don't remove or alter this PHPDoc
 */ 
class DB
{  
    /**
     * The singleton instance
     * 
     */
    public $connection; 
    static public $DBInstance; 
    
    public static function getDB(){
	    if(!self::$DBInstance) { 
	    	self::$DBInstance = new DB();
	    }
	    return self::$DBInstance;
    }
     
  	/**
  	 * Creates a PDO instance representing a connection to a database and makes the instance available as a singleton
  	 * 
  	 * @param string $dsn The full DSN, eg: mysql:host=localhost;dbname=testdb
  	 * @param string $username The user name for the DSN string. This parameter is optional for some PDO drivers.
  	 * @param string $password The password for the DSN string. This parameter is optional for some PDO drivers.
  	 * @param array $driver_options A key=>value array of driver-specific connection options
  	 * 
  	 * @return PDO
  	 */
    public function __construct() 
    {
	    try {
	    	$server = Hidden::$SQL_SERVER;
	    	$db = Hidden::$SQL_DB;
		    $this->connection = new PDO("mysql:host=$server;port=3306;dbname=$db;charset=utf8mb4", Hidden::$SQL_USER, Hidden::$SQL_PASS);
		    $this->connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		    $this->connection->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
		    $this->connection->setAttribute(PDO::MYSQL_ATTR_INIT_COMMAND, 'SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci');
		    
		} catch (PDOException $e) { 
		   die("PDO CONNECTION ERROR: " . $e->getMessage() . "<br/>");
		}   	    	
    }
    
    public function __destruct()
    {
	    $this->connection = NULL;
    }
	 
    /**
     * Executes an SQL statement, returning a result set as a PDOStatement object
     *
     * @param string $statement
     * @return PDOStatement
     */
    public function query($sql, $parameters = array()) {
  	   	$stmt = $this->connection->prepare($sql);
  	   	$stmt->execute($parameters);
  	   	
    	return $stmt;
    }
    
  }
?>