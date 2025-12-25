<?php
include 'connection.php';

$user_id = $_POST['user_id'];
$name = $_POST['name'];
$phone = $_POST['phone'];
$email = $_POST['email'];

// DEBUG LOGGING
file_put_contents("debug_profile.txt", "ID: $user_id, Name: $name, Phone: $phone, Email: $email\n", FILE_APPEND);

// Update query
$sql = "UPDATE users SET username = '$name', phone = '$phone', email = '$email' WHERE id = '$user_id'";

if(mysqli_query($connection, $sql)){
    echo json_encode(array("status" => "success", "message" => "Profile updated successfully"));
} else {
    file_put_contents("debug_profile.txt", "Error: " . mysqli_error($connection) . "\nSQL: $sql\n", FILE_APPEND);
    echo json_encode(array("status" => "error", "message" => "Failed to update profile"));
}
?>
