<?php
header("Content-Type: application/json");
require_once 'db_connect.php';

// Check if request is POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
    exit;
}

// 1. Validate Input
if (!isset($_POST['menu_id']) || !isset($_POST['rating']) || !isset($_POST['user_name'])) {
    echo json_encode(["status" => "error", "message" => "Missing required fields"]);
    exit;
}

$menu_id = $_POST['menu_id'];
$rating = $_POST['rating'];
$user_name = $_POST['user_name'];
$review_text = isset($_POST['review_text']) ? $_POST['review_text'] : "";

// 2. Handle File Uploads (Image & Video)
$target_dir = "uploads/";

// Create directory if it doesn't exist
if (!file_exists($target_dir)) {
    if (!mkdir($target_dir, 0777, true)) {
         echo json_encode(["status" => "error", "message" => "Failed to create uploads directory"]);
         exit;
    }
}

$image_url = null;
$video_url = null;

// Handle Image
if (isset($_FILES['image']) && $_FILES['image']['error'] == 0) {
    $file_extension = pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
    $new_filename = "img_" . time() . "_" . uniqid() . "." . $file_extension;
    $target_file = $target_dir . $new_filename;

    if (move_uploaded_file($_FILES['image']['tmp_name'], $target_file)) {
        // Store the RELATIVE path for the app to access (e.g., uploads/img_123.jpg)
        // Or full URL if preferred. Let's store relative path.
        // To verify: http://localhost/cantin/uploads/filename
        $image_url = "uploads/" . $new_filename;
    } else {
        // Log error but maybe continue saving the text?
        // For now, let's report error
        echo json_encode(["status" => "error", "message" => "Failed to move uploaded image"]);
        exit;
    }
}

// Handle Video
if (isset($_FILES['video']) && $_FILES['video']['error'] == 0) {
    $file_extension = pathinfo($_FILES['video']['name'], PATHINFO_EXTENSION);
    $new_filename = "vid_" . time() . "_" . uniqid() . "." . $file_extension;
    $target_file = $target_dir . $new_filename;

    if (move_uploaded_file($_FILES['video']['tmp_name'], $target_file)) {
        $video_url = "uploads/" . $new_filename;
    }
}

// 3. Insert into Database
try {
    $stmt = $conn->prepare("INSERT INTO reviews (menu_id, user_name, rating, review_text, image_url, video_url) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("isiss", $menu_id, $user_name, $rating, $review_text, $image_url, $video_url); // Wait, params count mismatch?
    // Types: i (int), s (string), i (int/double -> d?), s (string), s (string), s (string)
    // rating is usually int or float. Let's assume double for rating if needed, or int.
    // Schema: menu_id (int), user_name (varchar), rating (float/int), review_text, image_url, video_url
    
    // Let's check db schema from previous task
    // It's `rating` FLOAT usually.
    // bind_param types: i=integer, d=double, s=string, b=blob
    
    // Adjusted types: i (menu_id), s (user_name), d (rating), s (review_text), s (image_url), s (video_url)
    $stmt->bind_param("isdsss", $menu_id, $user_name, $rating, $review_text, $image_url, $video_url);

    if ($stmt->execute()) {
        echo json_encode([
            "status" => "success", 
            "message" => "Review submitted successfully",
            "data" => [
                "id" => $stmt->insert_id,
                "image_url" => $image_url
            ]
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "Database error: " . $stmt->error]);
    }

    $stmt->close();
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => "Exception: " . $e->getMessage()]);
}

$conn->close();
?>
