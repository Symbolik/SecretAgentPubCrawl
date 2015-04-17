<?php
$body = $app->request()->getBody();
$file= 'testfile.txt';
$current = file_get_contents($file);
$current .= "hello/n";
file_put_contents($file, $current);



?>