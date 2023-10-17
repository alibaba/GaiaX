//! #[cxx::bridge]
#[cxx::bridge]
mod ffi{
    extern "Rust"{
        fn print_message_in_rust();
    }
}

fn print_message_in_rust(){
    println!("Here is a test for cpp call Rust.");
}
