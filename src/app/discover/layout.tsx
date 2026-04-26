import { DiscoverySidebar } from "@/components/discover/DiscoverySidebar";
import { Header } from "@/components/layout/Header";

export default function DiscoverLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex flex-col bg-background relative">
      <Header />
      <div className="flex flex-grow transition-all duration-500 pt-20">
        <DiscoverySidebar />
        <main className="flex-1 p-4 sm:p-6 md:p-10 max-w-7xl mx-auto overflow-y-auto w-full">
          {children}
        </main>
      </div>
    </div>
  );
}
