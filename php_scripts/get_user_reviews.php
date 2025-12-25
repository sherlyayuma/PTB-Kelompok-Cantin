<?php
include 'connection.php';

$user_id = $_GET['user_id'];

// Assuming reviews table links to menu table
$query = "SELECT r.*, m.name as menu_name, m.image as menu_image, m.price as menu_price 
          FROM reviews r 
          JOIN menu m ON r.menu_id = m.id 
          WHERE r.user_id = '$user_id' OR r.user_name = (SELECT name FROM users WHERE id = '$user_id')
          ORDER BY r.created_at DESC";

$result = mysqli_query($connection, $query);
$reviews = array();

while($row = mysqli_fetch_assoc($result)) {
    // Map database image path to full URL if needed
    if ($row['image']) {
        $row['image_url'] = "http://192.168.1.3/cantin/" . $row['image'];
    }
    // Consistent field mapping
    $reviews[] = $row;
}

echo json_encode($reviews);
?>
