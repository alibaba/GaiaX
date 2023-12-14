// //! #[cxx::bridge]
// #[cxx::bridge]
// mod ffi{
//     extern "Rust"{
//         fn print_message_in_rust();
//     }
// }

// fn print_message_in_rust(){
//     println!("Here is a test for cpp call Rust.");
// }
#[no_mangle]
pub extern "C" fn hello_from_rust() {
    println!("Hello from Rust!");
}

#[no_mangle]
pub extern "C" fn hello2_from_rust(a:i32, b:i32)-> i32 {
    return a + b;
}