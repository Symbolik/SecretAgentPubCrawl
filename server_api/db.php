<?php
function getDB() {
$dbhost="localhost";
$dbuser="babelcen_sagent";
$dbpass="qTLxqbl#6h}o";
$dbname="babelcen_secret_agent";
$dbConnection = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbuser, $dbpass); 
$dbConnection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
return $dbConnection;
}
?>