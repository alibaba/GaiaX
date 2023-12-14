#[no_mangle]
pub extern "C" fn hello_from_rust() {
    println!("Hello from Rust!");
}

#[no_mangle]
pub extern "C" fn hello2_from_rust(a:i32, b:i32)-> i32 {
    return a + b;
}